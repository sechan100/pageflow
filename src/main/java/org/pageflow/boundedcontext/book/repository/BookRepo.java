package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface BookRepo extends JpaRepository<Book, Long> {

}
