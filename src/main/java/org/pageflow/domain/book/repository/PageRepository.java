package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {
}
