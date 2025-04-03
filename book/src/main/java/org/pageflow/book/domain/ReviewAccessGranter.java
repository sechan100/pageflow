package org.pageflow.book.domain;

import org.pageflow.book.domain.entity.Review;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
public class ReviewAccessGranter {
  private final UID reviewWriterId;

  public ReviewAccessGranter(Review review) {
    this.reviewWriterId = review.getWriter().getUid();
  }

  public boolean isWriter(UID userId) {
    return reviewWriterId.equals(userId);
  }
}
