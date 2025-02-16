package org.pageflow.book.persistence;

import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.NodeProjection;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeRepository extends BaseJpaRepository<TocNode, UUID> {
  @Query("SELECT new org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeProjection(n.id, n.parentNode.id, n.ov, n.title, TYPE(n)) FROM NodeJpaEntity n WHERE n.book.id = :bookId")
  List<NodeProjection> queryNodesByBookId(@Param("bookId") UUID bookId);

  @Query("""
      SELECT MAX(n.ov)
      FROM NodeJpaEntity n
      WHERE n.book.id = :bookId
      AND n.parentNode.id = :parentId
    """)
  Optional<Integer> findMaxOvAmongSiblings(@Param("bookId") UUID bookId, @Param("parentId") UUID parentId);

  @Query("""
      SELECT n
      FROM FolderJpaEntity n
      WHERE n.book.id = :bookId
      AND n.parentNode IS NULL
    """)
  Optional<Folder> findRootNode(@Param("bookId") UUID bookId);

  @Query("""
    SELECT siblings
    FROM NodeJpaEntity siblings
    WHERE siblings.parentNode.id = (
        SELECT node.parentNode.id
        FROM NodeJpaEntity node
        WHERE node.id = :nodeId
    )
  """)
  List<TocNode> findNodeWithSiblings(UUID nodeId);

  List<TocNode> findChildrenByParentNode_IdOrderByOv(UUID parentId);
}
