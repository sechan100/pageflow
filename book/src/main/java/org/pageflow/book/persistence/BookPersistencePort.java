package org.pageflow.book.persistence;

import org.pageflow.book.domain.book.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookPersistencePort extends JpaRepository<Book, UUID> {
  @Query("select b from Book b where b.author.id = :authorId")
  List<Book> findBooksByAuthorId(@Param("authorId") UUID authorId);

  @Query("""
    select b from Book b
    where b.author.id = :authorId
      and b.status = 'PUBLISHED'
    """)
  List<Book> findPublishedBooksByAuthorId(@Param("authorId") UUID authorId);

  @EntityGraph(attributePaths = {"author"})
  Optional<Book> findBookWithAuthorById(UUID bookId);
}
