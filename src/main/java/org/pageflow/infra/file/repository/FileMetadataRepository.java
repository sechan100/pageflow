package org.pageflow.infra.file.repository;

import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findAllByOwnerIdAndOwnerEntityTypeAndFileMetadataType(String id, String simpleName, FileMetadataType fileMetadataType);
    
    void deleteByManagedFilename(String managedFilename);
}
