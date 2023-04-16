package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.TocFolder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocFolderPersistencePort extends JpaRepository<TocFolder, UUID> {
  @EntityGraph(attributePaths = {"children"})
  Optional<TocFolder> findWithChildrenById(UUID id);

  @Query("""
    SELECT f
    FROM TocFolder f
    WHERE f.book.id = :bookId
    AND f.isEditable = :isEditable
    AND f.parentNode IS NULL
    AND f.title = :rootNodeTitle
    """)
  Optional<TocFolder> findRootFolder(
    @Param("bookId") UUID bookId,
    @Param("isEditable") boolean isEditable,
    @Param("rootNodeTitle") String rootNodeTitle
  );
}