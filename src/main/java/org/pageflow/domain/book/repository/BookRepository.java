package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.user.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
