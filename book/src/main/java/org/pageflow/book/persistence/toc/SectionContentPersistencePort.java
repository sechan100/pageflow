package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.SectionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SectionContentPersistencePort extends JpaRepository<SectionContent, UUID> {
  @Query("""
    SELECT SUM(c.charCount) FROM TocSection s
    JOIN s.content c WHERE s.book.id = :bookId
    """)
  int sumCharCountByBookId(@Param("bookId") UUID bookId);
}
