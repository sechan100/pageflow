package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : sechan
 */
public interface NodeJpaRepository extends BaseJpaRepository<NodeJpaEntity, Long> {
     @Query("SELECT new org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeProjection(n.id, n.parentNode.id, n.ov, n.title, TYPE(n)) FROM NodeJpaEntity n WHERE n.book.id = :bookId")
     List<NodeProjection> queryNodesByBookId(@Param("bookId") Long bookId);
}
