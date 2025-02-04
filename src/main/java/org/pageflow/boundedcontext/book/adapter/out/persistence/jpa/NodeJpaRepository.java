package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodeJpaRepository extends BaseJpaRepository<NodeJpaEntity, Long> {
     @Query("SELECT new org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeProjection(n.id, n.parentNode.id, n.ov, n.title, TYPE(n)) FROM NodeJpaEntity n WHERE n.book.id = :bookId")
     List<NodeProjection> queryNodesByBookId(@Param("bookId") Long bookId);

     @Query("""
         SELECT MAX(n.ov)
         FROM NodeJpaEntity n
         WHERE n.book.id = :bookId
         AND n.parentNode.id = :parentId
     """)
     Optional<Integer> findMaxOvAmongSiblings(@Param("bookId") Long bookId, @Param("parentId") Long parentId);

     @Query("""
         SELECT n
         FROM FolderJpaEntity n
         WHERE n.book.id = :bookId
         AND n.parentNode IS NULL
     """)
     Optional<FolderJpaEntity> findRootNode(@Param("bookId") Long bookId);
}
