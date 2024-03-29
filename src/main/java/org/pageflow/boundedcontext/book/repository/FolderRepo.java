package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface FolderRepo extends JpaRepository<Folder, Long> {
}
