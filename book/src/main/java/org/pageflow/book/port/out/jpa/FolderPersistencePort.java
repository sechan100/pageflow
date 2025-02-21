package org.pageflow.book.port.out.jpa;


import org.pageflow.book.domain.entity.Folder;
import org.pageflow.common.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface FolderPersistencePort extends BaseJpaRepository<Folder, UUID> {
}
