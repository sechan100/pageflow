package org.pageflow.infra.file.service;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.entity.BaseEntity;
import org.pageflow.global.entity.DefaultBaseEntity;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.exception.FileProcessingException;
import org.pageflow.infra.file.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class FileServiceImpl implements FileService {
    
    private final FileMetadataRepository fileRepository;
    private final String uploadDirectoryPrefix;
    private final String webUrlPrefix;

    // AllArgsConstructor
    public FileServiceImpl(CustomProps props, FileMetadataRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.uploadDirectoryPrefix = props.files().img().directory();
        this.webUrlPrefix = props.files().img().webUrlPrefix();
    }
    
    
    @Override
    @Transactional
    public <E extends BaseEntity> FileMetadata upload(MultipartFile file, E ownerEntity, Object ownerId, FileMetadataType fileMetadataType) {
        Assert.notNull(file, "file must not be null");
        Assert.notNull(ownerEntity, "ownerEntity must not be null");
        Assert.notNull(ownerId, "ownerId must not be null");
        Assert.notNull(fileMetadataType, "fileMetadataType must not be null");
        
        String pathPrefix = getDailyPathPrefix();
        String originalFilename = file.getOriginalFilename();
        Assert.notNull(originalFilename, "originalFilename must not be null");
        
        String extension = extractExtension(originalFilename);
        String UUIDfilename = UUID.randomUUID() + "." + extension;
        
        // ownerId의 타입이 Long, Integer, String 셋중 하나가 아니라면 예외
        if(!isAllowedOwnerIdType(ownerId)) {
            throw new IllegalArgumentException("ownerId의 타입은 Long, Integer, String만 가능합니다.");
        }
        
        // DB에 파일 저장 정보 기록
        FileMetadata fileMetadata = FileMetadata.builder()
                // /{uploadDirectoryPrefix}/{y}/{m}/{d}/{UUID}.{ext}
                .uploadDirectory(uploadDirectoryPrefix) // /{uploadDirectoryPrefix}
                .managedFilename(UUIDfilename)  // {UUID}.{ext}
                .originalFilename(originalFilename) // {originalFilename}.{ext}
                .ownerEntityType(ownerEntity.getClass().getSimpleName()) // ex) Profile
                .ownerId(ownerId.toString())
                .originalExtension(extension)
                .size(file.getSize())
                .fileMetadataType(fileMetadataType)
                .pathPrefix(pathPrefix) // /{y}/{m}/{d}
                .build();
        fileRepository.save(fileMetadata);
        
        try {
            File uploadDirectoryFile = new File(uploadDirectoryPrefix + pathPrefix);
            // 파일을 저장할 디렉토리가 없다면 생성(/{uploadDirectoryPrefix}/{y}/{m}/{d})
            if (!uploadDirectoryFile.exists()) {
                boolean mkdirSuccess = uploadDirectoryFile.mkdirs();
            }
            
            String uploadDirectoryFullPath = uploadDirectoryPrefix + pathPrefix + "/" + UUIDfilename;
            
            // 파일 저장
            File fileToStore = new File(uploadDirectoryFullPath);
            file.transferTo(fileToStore);
        } catch(Exception e) {
            log.error("'{}' 파일 저장 중 오류가 발생했습니다: {}", fileMetadata.getUploadDirectory(), e.getMessage());
            throw new FileProcessingException("fail to store file: " + e.getMessage());
        }
        
        return fileMetadata;
    }
    
    @Override
    public <E extends DefaultBaseEntity> FileMetadata upload(MultipartFile file, E ownerEntity, FileMetadataType fileMetadataType) {
        return upload(file, ownerEntity, ownerEntity.getId(), fileMetadataType);
    }
    
    /**
     * @param webOrServerFilePath /{y}/{m}/{d}/{UUID}.{ext}
     */
    @Override
    @Transactional
    public void delete(String webOrServerFilePath) {
        Assert.notNull(webOrServerFilePath, "filePath must not be null");
        
        // 파일 삭제
        String pureFilePath = extractPureFilePath(webOrServerFilePath);
        File file = new File(uploadDirectoryPrefix + pureFilePath);
        boolean deleteSuccess = file.delete();
        if(!deleteSuccess) {
            log.error("파일 삭제에 실패: {}", file);
            throw new FileProcessingException("파일 삭제에 실패했습니다.");
        }
        
        try {
            // FileMetadata 삭제
            String managedFilename = pureFilePath.substring(pureFilePath.lastIndexOf("/") + 1);
            fileRepository.deleteByManagedFilename(managedFilename);
        } catch(Exception e){
            throw new FileProcessingException("파일 메타데이터 삭제에 실패했습니다.");
        }
    }

    @Override
    public List<FileMetadata> getFileMetadatas(Object ownerId, Class ownerEntityType, FileMetadataType fileMetadataType) {
        Assert.notNull(ownerId, "ownerId must not be null");
        Assert.notNull(ownerEntityType, "ownerEntityType must not be null");
        Assert.notNull(fileMetadataType, "fileMetadataType must not be null");
        
        // ownerId 타입 검사
        if(!isAllowedOwnerIdType(ownerId)) {
            throw new IllegalArgumentException("ownerId의 타입은 Long, Integer, String만 가능합니다.");
        }
        
        // ownerEntityType 검사
        if(!BaseEntity.class.isAssignableFrom(ownerEntityType)) {
            throw new IllegalArgumentException("ownerEntityType은 BaseEntity의 구현체만 가능합니다.");
        }
        
        return fileRepository.findAllByOwnerIdAndOwnerEntityTypeAndFileMetadataType(
                ownerId.toString(),
                ownerEntityType.getSimpleName(),
                fileMetadataType
        );
    }
    
    /**
     * @param fileMetadata 파일 메타데이터
     * @return 호스트에서 바로 접근 가능한 uri: /{webUrlPrefix}/{y}/{m}/{d}/{UUID}.{ext}
     */
    @Override
    public String getWebFilePath(FileMetadata fileMetadata) {
        Assert.notNull(fileMetadata, "fileMetadata must not be null");
        return webUrlPrefix + fileMetadata.getPathPrefix() + "/" + fileMetadata.getManagedFilename();
    }
    
    /**
     * 파일의 확장자를 추출
     * @throws IllegalArgumentException 파일명에 확장자가 없는 경우
     */
    private String extractExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if (extension.isEmpty()) {
            // .을 기준으로 끊은 확장자가 존재하지 않음
            throw new IllegalArgumentException("파일명에 확장자가 없습니다.");
        } else {
            return extension;
        }
    }
    
    /**
     * @return /{y}/{m}/{d}
     */
    private String getDailyPathPrefix() {
        LocalDateTime now = LocalDateTime.now();
        return "/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth();
    }
    
    /**
     * 경로가 /{webUrlPrefix}/{y}/{m}/{d}/{UUID}.{ext}의 형태인지 확인
     */
    private boolean isWebFilePath(String path) {
        // 1. 문자열이 /{webUrlPrefix}로 시작하는지
        if(!path.startsWith(webUrlPrefix)) {
            return false;
        }
        
        // prefix를 자른 문자열이 /{y}/{m}/{d}/{UUID}.{ext}의 형태인지 확인
        String pathWithoutPrefix = path.substring(webUrlPrefix.length());
        return isPureFilePath(pathWithoutPrefix);
    }
    
    /**
     * 경로가 {uploadDirectoryPrefix}/{y}/{m}/{d}/{UUID}.{ext}의 형태인지 확인
     */
    private boolean isServerFilePath(String path) {
        // 1. 문자열이 {uploadDirectoryPrefix}로 시작하는지
        if(!path.startsWith(uploadDirectoryPrefix)) {
            return false;
        }
        
        // prefix를 자른 문자열이 /{y}/{m}/{d}/{UUID}.{ext}의 형태인지 확인
        String pathWithoutPrefix = path.substring(uploadDirectoryPrefix.length());
        return isPureFilePath(pathWithoutPrefix);
    }
    
    /**
     * 경로가 /{y}/{m}/{d}/{UUID}.{ext}의 형태인지 확인
     */
    private boolean isPureFilePath(String path){
        // 문자열이 /{y}/{m}/{d}/{UUID}.{ext}의 형태인지 확인
        return path.matches("^/\\d{4}/\\d{1,2}/\\d{1,2}/[\\w\\-]+\\.\\w+$");
    }
    
    /**
     * @param webOrServerFilePath webFilePath 또는 serverFilePath
     * @return pureFilePath: /{y}/{m}/{d}/{UUID}.{ext}
     * @throws IllegalArgumentException webOrServerFilePath가 /{webUrlPrefix}/{y}/{m}/{d}/{UUID}.{ext} 또는 {uploadDirectoryPrefix}/{y}/{m}/{d}/{UUID}.{ext}의 형태가 아닌 경우
     */
    private String extractPureFilePath(String webOrServerFilePath){
        // webFilePath
        if(isWebFilePath(webOrServerFilePath)){
            return webOrServerFilePath.substring(webUrlPrefix.length());
            
        // serverFilePath
        } else if(isServerFilePath(webOrServerFilePath)){
            return webOrServerFilePath.substring(uploadDirectoryPrefix.length());
        
        // 예외
        } else {
            throw new IllegalArgumentException("webOrServerFilePath가 /{webUrlPrefix}/{y}/{m}/{d}/{UUID}.{ext} 또는 {uploadDirectoryPrefix}/{y}/{m}/{d}/{UUID}.{ext}의 형태가 아닙니다.");
        }
    }
    
    private boolean isAllowedOwnerIdType(Object id) {
        return id instanceof Long || id instanceof Integer || id instanceof String;
    }
}
