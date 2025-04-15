package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.TocFolder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocFolderPersistencePort extends JpaRepository<TocFolder, UUID> {
  @EntityGraph(attributePaths = {"children"})
  Optional<TocFolder> findWithChildrenById(UUID id);
}