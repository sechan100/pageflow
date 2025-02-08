package org.pageflow.boundedcontext.book.persistence;


import org.pageflow.boundedcontext.book.domain.entity.Folder;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface FolderRepository extends BaseJpaRepository<Folder, UUID> {
}
