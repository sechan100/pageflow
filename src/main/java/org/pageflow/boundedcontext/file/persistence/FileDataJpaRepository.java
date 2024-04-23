package org.pageflow.boundedcontext.file.persistence;

import org.pageflow.boundedcontext.file.model.FileIdentity;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileDataJpaRepository extends BaseJpaRepository<FileData, String> {

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
        @Param("ownerId") Long ownerId,
        @Param("ownerType") FileOwnerType ownerType);
}

