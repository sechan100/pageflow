package org.pageflow.domain.book.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.pageflow.domain.book.entity.Book;
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

}
