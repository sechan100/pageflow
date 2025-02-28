package org.pageflow.book.port.out.jpa;

import org.pageflow.book.domain.entity.Review;
import org.pageflow.common.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface ReviewPersistencePort extends BaseJpaRepository<Review, UUID> {
}
