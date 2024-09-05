package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : sechan
 */
public interface BookJpaRepository extends BaseJpaRepository<BookJpaEntity, Long> {
    @EntityGraph(attributePaths = {"author"})
    @Query("select b from BookJpaEntity b where b.author.id = :authorId")
    List<BookJpaEntity> findWithAuthorByAuthorId(@Param("authorId") Long authorId);
}
