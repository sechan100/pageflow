package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.review.ReviewPermission;
import org.pageflow.book.domain.entity.Review;
import org.pageflow.book.port.in.review.ReviewAccessPermitter;
import org.pageflow.book.port.out.jpa.ReviewPersistencePort;
import org.pageflow.common.permission.ResourcePermissionContext;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultReviewAccessPermitter implements ReviewAccessPermitter {
  private final ResourcePermissionContext resourcePermissionContext;
  private final ReviewPersistencePort reviewPersistencePort;

  private ReviewPermission _grant(UUID reviewId, UID uid) {
    Review review = reviewPersistencePort.findById(reviewId).get();
    UID writerId = review.getWriter().getUid();
    if(writerId.equals(uid)) {
      return ReviewPermission.writer(reviewId);
    } else {
      return ReviewPermission.reader(reviewId);
    }
  }

  @Override
  public void setPermission(UUID reviewId, UID uid) {
    ReviewPermission permission = _grant(reviewId, uid);
    resourcePermissionContext.addResourcePermission(permission);
  }
}