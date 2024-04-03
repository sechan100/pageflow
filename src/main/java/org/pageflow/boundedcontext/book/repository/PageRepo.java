package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface PageRepo extends JpaRepository<PageEntity, Long> {
}
