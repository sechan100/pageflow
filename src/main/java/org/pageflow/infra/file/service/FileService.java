package org.pageflow.infra.file.service;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    
    private final CustomProperties customProperties;
    private final FileMetadataRepository fileRepository;
    private String uploadDirectoryPath;
    
    
    public FileService(CustomProperties customProperties, FileMetadataRepository fileRepository) {
        this.customProperties = customProperties;
        this.fileRepository = fileRepository;
        
        String uploadDirectoryPath = customProperties.getFiles().getImg().getDirectory();
        if(!uploadDirectoryPath.endsWith("/")) {
            uploadDirectoryPath += "/";
        }
        this.uploadDirectoryPath = uploadDirectoryPath;
    }
    
    public <E extends BaseEntity> FileMetadata uploadFile(MultipartFile fileToUpload, E ownerEntity, FileMetadataType fileMetadataType) {
        
        String pathPrefix = getDailyPathPrefix();
        String originalFilename = fileToUpload.getOriginalFilename();
        if(originalFilename == null) {
            throw new IllegalArgumentException("올바르지 않은 파일명입니다.");
        }
        String extension = extractExtension(originalFilename);
        String UUIDfilename = UUID.randomUUID() + "." + extension;
        
        
        
        // DB에 파일 저장 정보 기록
        FileMetadata fileMetadata = FileMetadata.builder()
                // {uploadDirectory}/{y}/{m}/{d}/{UUID}.{ext}
                .uploadDirectory(uploadDirectoryPath) // {uploadDirectory}/
                .managedFilename(UUIDfilename)  // {UUID}.{ext}
                .originalFilename(originalFilename) // {originalFilename}.{ext}
                .ownerEntityType(ownerEntity.getClass().getSimpleName()) // ex) Profile
                .ownerId(ownerEntity.getId())
                .originalExtension(extension)
                .size(fileToUpload.getSize())
                .fileMetadataType(fileMetadataType)
                .pathPrefix(pathPrefix) // {y}/{m}/{d}/
                .build();
        fileRepository.save(fileMetadata);
        
        // 파일 저장 디렉토리 생성   {uploadDirectory}/{y}/{m}/{d}/
        File uploadDirectoryFile = new File(uploadDirectoryPath + pathPrefix);
        if(!uploadDirectoryFile.exists()) {
            boolean mkdirSuccess = uploadDirectoryFile.mkdirs();
        }
        
        String uploadDirectoryFullPath = uploadDirectoryPath + pathPrefix + UUIDfilename;
        
        // 파일 저장
        File fileToStore = new File(uploadDirectoryFullPath);
        try {
            fileToUpload.transferTo(fileToStore);
        } catch (IOException e) {
            log.info("'{}' 파일 저장 중 오류가 발생했습니다.", fileMetadata.getUploadDirectory());
        }
        
        return fileMetadata;
    }
    
    /**
     * @param filePath /{y}/{m}/{d}/{UUID}.{ext}
     * @return 파일 삭제 성공 여부
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        boolean deleteSuccess = file.delete();
        String pathPrefix = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        String managedFilename = filePath.substring(filePath.lastIndexOf("/") + 1);
        
        if(deleteSuccess){
            fileRepository.findByPathPrefixAndManagedFilename(pathPrefix, managedFilename)
                    .ifPresent(fileRepository::delete);
            return true;
        } else {
            log.info("'{}' 삭제할 파일의 경로를 찾을 수 없습니다.", filePath);
            return false;
        }
    }
    
    public List<FileMetadata> getFileMetadatas(BaseEntity ownerEntity, FileMetadataType fileMetadataType) {
        return fileRepository.findAllByOwnerIdAndOwnerEntityTypeAndFileMetadataType(
                ownerEntity.getId(),
                ownerEntity.getClass().getSimpleName(),
                fileMetadataType
        );
    }
    
    public String extractExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        if(extension.isEmpty()) {
            throw new IllegalArgumentException("파일명에 확장자가 없거나, 올바르지 않은 파일명입니다.");
        } else {
            return extension;
        }
    }
    
    /**
     * @return {y}/{m}/{d}/
     */
    public String getDailyPathPrefix() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/";
    }
    
    
    public String getImgUri(FileMetadata fileMetadata) {
        String imgResourcePathPrefix = customProperties.getFiles().getImg().getBaseUrl();
        if(!imgResourcePathPrefix.endsWith("/")) {
            imgResourcePathPrefix += "/";
        }
        
        return  imgResourcePathPrefix +
                fileMetadata.getPathPrefix() +
                fileMetadata.getManagedFilename();
    }
}
