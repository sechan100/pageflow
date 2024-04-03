package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface FolderRepo extends JpaRepository<FolderEntity, Long> {
}
