package org.pageflow.file.persistence;

import org.pageflow.common.shared.jpa.repository.BaseJpaRepository;
import org.pageflow.file.model.FileIdentity;
import org.pageflow.file.shared.FileOwnerType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FileDataJpaRepository extends BaseJpaRepository<FileData, UUID> {

  @Query("""
        select f from FileData f
        where f.ownerId = :#{#identity.ownerId.toLong()}
        and f.ownerType = :#{#identity.ownerType}
        and f.fileType = :#{#identity.fileType}
    """)
  List<FileData> findAll(@Param("identity") FileIdentity identity);

  @Query("""
        delete from FileData f
        where f.ownerId = :#{#identity.ownerId.toLong()}
        and f.ownerType = :#{#identity.ownerType}
        and f.fileType = :#{#identity.fileType}
    """)
  void deleteAll(@Param("identity") FileIdentity identity);


  @Query("""
        delete from FileData f
        where f.ownerId = :#{#ownerId.toLong()}
        and f.ownerType = :ownerType
    """)
  void deleteAll(
    @Param("ownerId") UUID ownerId,
    @Param("ownerType") FileOwnerType ownerType);
}

