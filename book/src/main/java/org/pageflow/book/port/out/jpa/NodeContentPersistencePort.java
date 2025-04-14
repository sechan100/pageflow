package org.pageflow.book.port.out.jpa;

import org.pageflow.book.domain.entity.NodeContent;
import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeContentPersistencePort extends BaseJpaRepository<NodeContent, UUID> {

  @Query("SELECT SUM(c.charCount) FROM NodeContent c WHERE c.book.id = :bookId")
  int sumCharCountByBookId(@Param("bookId") UUID bookId);
}
