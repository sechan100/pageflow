package org.pageflow.boundedcontext.book.repository;

import org.pageflow.boundedcontext.book.entity.OutlineNode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : sechan
 */
public interface OutlineNodeRepo extends JpaRepository<OutlineNode, Long> {
}
