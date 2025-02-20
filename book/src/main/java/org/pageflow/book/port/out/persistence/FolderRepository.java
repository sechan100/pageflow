package org.pageflow.book.port.out.persistence;


import org.pageflow.book.domain.entity.Folder;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface FolderRepository extends BaseJpaRepository<Folder, UUID> {
}
