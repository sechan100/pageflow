package org.pageflow.file.repository;

import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.pageflow.file.entity.FileData;
import org.pageflow.file.shared.FileType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FileDataJpaRepository extends BaseJpaRepository<FileData, UUID> {

  @Query("""
        select f from FileData f
        where f.ownerId = :ownerId
        and f.fileType = :fileType
    """)
  List<FileData> findAll(@Param("ownerId") String ownerId, @Param("fileType") FileType fileType);

  @Query("""
        delete from FileData f
        where f.ownerId = :ownerId
        and f.fileType = :fileType
    """)
  void deleteAll(@Param("ownerId") String ownerId, @Param("fileType") FileType fileType);
  
  List<FileData> findAllByOwnerIdAndFileType(@Param("ownerId") String ownerId, @Param("fileType") FileType fileType);
}

