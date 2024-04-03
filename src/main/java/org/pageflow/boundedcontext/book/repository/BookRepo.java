package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface BookRepo extends JpaRepository<BookEntity, Long> {

}
