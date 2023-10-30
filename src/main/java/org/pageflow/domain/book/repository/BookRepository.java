package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    @Modifying
    @Query("update Book b set b.view = b.view + 1 where b.id = :id")
    int updateView(@Param("id") Long id);
}
