package org.pageflow.infra.file.service;

import org.pageflow.base.entity.DefaultBaseEntity;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : sechan
 */
public interface FileService {

    <E extends DefaultBaseEntity> FileMetadata upload(MultipartFile file, E ownerEntity, FileMetadataType fileMetadataType);
    <E extends DefaultBaseEntity> String uploadAndGetUrl(MultipartFile file, E ownerEntity, FileMetadataType fileMetadataType);
    void delete(String filePath);
    List<FileMetadata> getFileMetadatas(DefaultBaseEntity ownerEntity, FileMetadataType fileMetadataType);
    String getUrl(FileMetadata fileMetadata);
    
}
