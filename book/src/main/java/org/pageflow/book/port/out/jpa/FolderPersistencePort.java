package org.pageflow.book.port.out.jpa;


import org.pageflow.book.domain.entity.Folder;
import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface FolderPersistencePort extends BaseJpaRepository<Folder, UUID> {
  @EntityGraph(attributePaths = {"children"})
  Folder findWithChildrenById(UUID folderId);
}
