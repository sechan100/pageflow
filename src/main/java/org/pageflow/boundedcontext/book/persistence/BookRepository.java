package org.pageflow.boundedcontext.book.persistence;

import org.pageflow.boundedcontext.book.domain.entity.Book;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookRepository extends BaseJpaRepository<Book, UUID> {
  @EntityGraph(attributePaths = {"author"})
  @Query("select b from BookJpaEntity b where b.author.id = :authorId")
  List<Book> findWithAuthorByAuthorId(@Param("authorId") UUID authorId);
}
