package org.pageflow.book.port.out.jpa;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookPersistencePort extends BaseJpaRepository<Book, UUID> {
  @Query("select b from Book b where b.author.id = :authorId")
  List<Book> findBooksByAuthorId(@Param("authorId") UUID authorId);

  @EntityGraph(attributePaths = {"author"})
  Optional<Book> findBookWithAuthorById(UUID bookId);
}
