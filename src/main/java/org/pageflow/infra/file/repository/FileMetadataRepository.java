package org.pageflow.infra.file.repository;

import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findAllByOwnerIdAndOwnerEntityTypeAndFileMetadataType(Long id, String simpleName, FileMetadataType fileMetadataType);

    /**
     * @param pathPrefix      {y}/{m}/{d}/
     * @param managedFilename {UUID}.{확장자}
     * @return Optional<FileMetadata>
     */
    Optional<FileMetadata> findByPathPrefixAndManagedFilename(String pathPrefix, String managedFilename);
}
