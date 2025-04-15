package org.pageflow.book.persistence;

import org.pageflow.book.domain.review.entity.Review;
import org.pageflow.common.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface ReviewPersistencePort extends BaseJpaRepository<Review, UUID> {
}
