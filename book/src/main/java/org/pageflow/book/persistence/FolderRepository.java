package org.pageflow.book.persistence;


import org.pageflow.book.domain.entity.Folder;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface FolderRepository extends BaseJpaRepository<Folder, UUID> {
}
