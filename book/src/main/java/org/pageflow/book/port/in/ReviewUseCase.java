package org.pageflow.book.port.in;

import org.pageflow.book.dto.ReviewDto;
import org.pageflow.book.port.in.cmd.AddReviewCmd;
import org.pageflow.book.port.in.cmd.UpdateReviewCmd;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface ReviewUseCase {
  ReviewDto addReview(AddReviewCmd cmd);
  ReviewDto updateReview(UpdateReviewCmd cmd);
  ReviewDto removeReview(UUID reviewId);
}
