package org.pageflow.infra.file.service;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.base.constants.CustomProps;
import org.pageflow.base.entity.DefaultBaseEntity;
import org.pageflow.base.exception.UserFeedbackException;
import org.pageflow.base.exception.code.CommonErrorCode;
import org.pageflow.base.exception.code.FileErrorCode;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.exception.FileProcessingException;
import org.pageflow.infra.file.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
@Slf4j
public class DefaultFileService implements FileService {

    private final CustomProps customProps;
    private final FileMetadataRepository fileRepository;
    private final String uploadDirectoryPath;

    // AllArgsConstructor
    public DefaultFileService(CustomProps customProps, FileMetadataRepository fileRepository) {
        this.customProps = customProps;
        this.fileRepository = fileRepository;
        this.uploadDirectoryPath = customProps.getFiles().getImg().getDirectory();
    }
    
    
    @Override
    public <E extends DefaultBaseEntity> FileMetadata upload(MultipartFile file, E ownerEntity, FileMetadataType fileMetadataType) {

        String pathPrefix = getDailyPathPrefix();
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new UserFeedbackException(FileErrorCode.BLANK_FILE_NAME);
        }
        String extension = extractExtension(originalFilename);
        String UUIDfilename = UUID.randomUUID() + "." + extension;


        // DB에 파일 저장 정보 기록
        FileMetadata fileMetadata = FileMetadata.builder()
                // {uploadDirectory}/{y}/{m}/{d}/{UUID}.{ext}
                .uploadDirectory(uploadDirectoryPath) // {uploadDirectory}
                .managedFilename(UUIDfilename)  // {UUID}.{ext}
                .originalFilename(originalFilename) // {originalFilename}.{ext}
                .ownerEntityType(ownerEntity.getClass().getSimpleName()) // ex) Profile
                .ownerId(ownerEntity.getId())
                .originalExtension(extension)
                .size(file.getSize())
                .fileMetadataType(fileMetadataType)
                .pathPrefix(pathPrefix) // /{y}/{m}/{d}
                .build();
        fileRepository.save(fileMetadata);
        
        try {
            // 파일 저장 디렉토리 생성   {uploadDirectory}/{y}/{m}/{d}
            File uploadDirectoryFile = new File(uploadDirectoryPath + pathPrefix);
            if (!uploadDirectoryFile.exists()) {
                boolean mkdirSuccess = uploadDirectoryFile.mkdirs();
            }
            
            String uploadDirectoryFullPath = uploadDirectoryPath + pathPrefix + "/" + UUIDfilename;
    
            // 파일 저장
            File fileToStore = new File(uploadDirectoryFullPath);
            file.transferTo(fileToStore);
        } catch(Exception e) {
            log.error("'{}' 파일 저장 중 오류가 발생했습니다: {}", fileMetadata.getUploadDirectory(), e.getMessage());
            throw new UserFeedbackException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return fileMetadata;
    }
    
    @Override
    public <E extends DefaultBaseEntity> String uploadAndGetUrl(MultipartFile file, E ownerEntity, FileMetadataType fileMetadataType) {
        FileMetadata fileMetadata = upload(file, ownerEntity, fileMetadataType);
        return getUrl(fileMetadata);
    }
    
    /**
     * @param filePath /{y}/{m}/{d}/{UUID}.{ext}
     */
    @Override
    public void delete(String filePath) {
        // filePath가 '/{y}/{m}/{d}/{UUID}.{ext}'의 형식이 아니라면 예외
        if(!filePath.matches("^/\\d{4}/\\d{1,2}/\\d{1,2}/[\\w\\-]+\\.\\w+$")) {
            throw new UserFeedbackException(FileErrorCode.INVALID_FILE_PATH, filePath);
        }
        
        File file = new File(customProps.getFiles().getImg().getDirectory() + filePath);
        boolean deleteSuccess = file.delete();
        if(!deleteSuccess) {
            log.error("파일 삭제에 실패: {}", file);
            throw new FileProcessingException("파일 삭제에 실패했습니다.");
        }
        
        String pathPrefix = filePath.substring(0, filePath.lastIndexOf("/"));
        String managedFilename = filePath.substring(filePath.lastIndexOf("/") + 1);
        
        try {
            FileMetadata fileMetadataToDelete = fileRepository.findByPathPrefixAndManagedFilename(pathPrefix, managedFilename);
            fileRepository.delete(fileMetadataToDelete);
        } catch(Exception e){
            throw new FileProcessingException("파일 메타데이터 삭제에 실패했습니다.");
        }
    }

    @Override
    public List<FileMetadata> getFileMetadatas(DefaultBaseEntity ownerEntity, FileMetadataType fileMetadataType) {
        return fileRepository.findAllByOwnerIdAndOwnerEntityTypeAndFileMetadataType(
                ownerEntity.getId(),
                ownerEntity.getClass().getSimpleName(),
                fileMetadataType
        );
    }

    public String extractExtension(String filename) throws UserFeedbackException {
        try {
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            if (extension.isEmpty()) {
                throw new UserFeedbackException(FileErrorCode.INVALID_FILE_EXTENSION, filename);
            } else {
                return extension;
            }
        } catch (Exception e) {
            throw new UserFeedbackException(FileErrorCode.INVALID_FILE_NAME, filename);
        }
    }

    /**
     * @return /{y}/{m}/{d}
     */
    public String getDailyPathPrefix() {
        LocalDateTime now = LocalDateTime.now();
        return "/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth();
    }
    
    /**
     * @param fileMetadata 파일 메타데이터
     * @return 호스트에서 바로 접근 가능한 uri: /{imgResourcePathPrefix}/{y}/{m}/{d}/{UUID}.{ext}
     */
    @Override
    public String getUrl(FileMetadata fileMetadata) {
        String imgResourcePathPrefix = customProps.getFiles().getImg().getBaseUrl();
        
        return imgResourcePathPrefix +
                fileMetadata.getPathPrefix() + "/" + fileMetadata.getManagedFilename();
    }
    
    /**
     * @param filePath /{imgResourcePathPrefix}/{y}/{m}/{d}/{UUID}.{ext}
     * @return  url상에서 접근 가능한 baseUrl prefix를 제거한 디렉토리상의 위치와 일치하는 파일 경로 /{y}/{m}/{d}/{UUID}.{ext}
     */
    public String getPureFilePath(String filePath) {
        String imgResourcePathPrefix = customProps.getFiles().getImg().getBaseUrl();
        return filePath.substring(imgResourcePathPrefix.length());
    }
}
