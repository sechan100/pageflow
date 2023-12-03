package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.dto.BookWithCommentCount;
import org.pageflow.domain.book.dto.BookWithPreferenceCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Book> findAll(Specification<Book> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Book> findBookWithAuthorById(Long id);

    @EntityGraph(attributePaths = {"author", "author.account", "chapters"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Book> findBookWithAuthorAndChapterById(Long id);
    
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Book> findAllByAuthorId(Long profileId);

    @Query("SELECT new org.pageflow.domain.book.dto.BookWithPreferenceCount(b AS book, COUNT(p) AS preferenceCount) " +
            "FROM Book b LEFT JOIN Preference p ON b.id = p.targetId AND p.targetType = 'Book' AND p.isLiked = true " +
            "GROUP BY b.id " +
            "ORDER BY COUNT(p) DESC")
    Slice<BookWithPreferenceCount> findAllBooksOrderByPreferenceCount(Specification<Book> spec, Pageable pageable);

    @Query("SELECT new org.pageflow.domain.book.dto.BookWithCommentCount(b AS book, COUNT(p) AS commentCount) " +
            "FROM Book b LEFT JOIN Comment p ON b.id = p.targetId AND p.targetType = 'Book'" +
            "GROUP BY b.id " +
            "ORDER BY COUNT(p) DESC")
    Slice<BookWithCommentCount> findAllBooksOrderByCommentCount(Specification<Book> spec, Pageable pageable);
}
