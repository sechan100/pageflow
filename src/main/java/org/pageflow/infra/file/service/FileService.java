package org.pageflow.infra.file.service;

import org.pageflow.global.data.Entity;
import org.pageflow.global.data.DefaultEntity;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * webFilePath(웹 파일경로): /{webUrlPrefix}/{y}/{m}/{d}/{UUID}.{ext}<br>
 * serverFilePath(서버 파일경로): {uploadDirectory}/{y}/{m}/{d}/{UUID}.{ext}<br>
 * pureFilePath(순수 파일경로): /{y}/{m}/{d}/{UUID}.{ext}<br>
 * 웹 파일경로는 웹에서 파일에 접근하기위한 경로이고, 서버 파일경로는 실제 서버상에서의 위치. 순수 파일경로는 웹 파일경로와 서버 파일경로에서 공통되는 부분이다.
 *
 * @author : sechan
 */
public interface FileService {
    
    /**
     * @param file 파일
     * @param ownerEntity 파일을 소유하는 엔티티
     * @param ownerId String, Long, Integer가 가능하다
     * @param fileMetadataType 파일 타입
     * @throws org.pageflow.infra.file.exception.FileProcessingException 파일 저장 실패
     */
    <E extends Entity> FileMetadata upload(MultipartFile file, E ownerEntity, Object ownerId, FileMetadataType fileMetadataType);
    
    <E extends DefaultEntity> FileMetadata upload(MultipartFile file, E ownerEntity, FileMetadataType fileMetadataType);
    
    /**
     * @param filePath 파일 경로
     * @throws org.pageflow.infra.file.exception.FileProcessingException 파일 삭제 실패
     */
    void delete(String filePath);
    
    String getWebFilePath(FileMetadata fileMetadata);
    
    List<FileMetadata> getFileMetadatas(Object ownerId, Class ownerEntityType, FileMetadataType fileMetadataType);
    
}
