package org.pageflow.book.port.out.jpa;

import org.pageflow.book.domain.entity.NodeContent;
import org.pageflow.common.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeContentPersistencePort extends BaseJpaRepository<NodeContent, UUID> {
}
