package org.pageflow.book.persistence;

import org.pageflow.book.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
