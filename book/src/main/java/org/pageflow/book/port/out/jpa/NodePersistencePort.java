package org.pageflow.book.port.out.jpa;

import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.NodeProjection;
import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodePersistencePort extends BaseJpaRepository<TocNode, UUID> {
  @Query("""
      SELECT new org.pageflow.book.domain.toc.NodeProjection(n.id, n.parentNode.id, n.ov, n.title, TYPE(n))
      FROM TocNode n
      WHERE n.book.id = :bookId
    """)
  List<NodeProjection> queryNodesByBookId(@Param("bookId") UUID bookId);

  @Query("""
      SELECT n
      FROM Folder n
      WHERE n.book.id = :bookId
      AND n.parentNode IS NULL
    """)
  Optional<Folder> findRootNode(@Param("bookId") UUID bookId);

  /**
   * @param bookId
   * @param isRevisionToc 개정 tree node들을 찾을 것인지 여부
   * @return
   */
  List<TocNode> findAllByBookIdAndIsRevisionToc(UUID bookId, boolean isRevisionToc);

  List<TocNode> findAllByBookId(UUID bookId);
}
