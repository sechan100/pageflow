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
  @Query("SELECT new org.pageflow.book.domain.toc.NodeProjection(n.id, n.parentNode.id, n.ov, n.title, TYPE(n)) FROM TocNode n WHERE n.book.id = :bookId")
  List<NodeProjection> queryNodesByBookId(@Param("bookId") UUID bookId);

  @Query("""
      SELECT MAX(n.ov)
      FROM TocNode n
      WHERE n.book.id = :bookId
      AND n.parentNode.id = :parentId
    """)
  Optional<Integer> findMaxOvAmongSiblings(@Param("bookId") UUID bookId, @Param("parentId") UUID parentId);

  @Query("""
      SELECT n
      FROM Folder n
      WHERE n.book.id = :bookId
      AND n.parentNode IS NULL
    """)
  Optional<Folder> findRootNode(@Param("bookId") UUID bookId);

  @Query("""
    SELECT siblings
    FROM TocNode siblings
    WHERE siblings.parentNode.id = (
        SELECT node.parentNode.id
        FROM TocNode node
        WHERE node.id = :nodeId
    )
  """)
  List<TocNode> findNodeWithSiblings(UUID nodeId);

  List<TocNode> findChildrenByParentNode_IdOrderByOv(UUID parentId);
}
