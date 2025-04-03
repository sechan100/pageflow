package org.pageflow.book.port.in.review;

import org.pageflow.book.dto.ReviewDto;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface ReviewUseCase {
  ReviewDto createReview(AddReviewCmd cmd);

  ReviewDto updateReview(UpdateReviewCmd cmd);

  void deleteReview(UUID reviewId);
}
