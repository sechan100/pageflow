package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface PageRepo extends JpaRepository<Page, Long> {
}
