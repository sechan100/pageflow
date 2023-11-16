package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);
    Slice<Book> findAll(Specification<Book> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.FETCH)
    Book findBookWithAuthorById(Long id);

    @EntityGraph(attributePaths = {"author", "author.account", "chapters"}, type = EntityGraph.EntityGraphType.FETCH)
    Book findBookWithAuthorAndChapterById(Long id);

}
