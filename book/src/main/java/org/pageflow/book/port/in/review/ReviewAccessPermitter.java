package org.pageflow.book.port.in.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.ReviewPermission;
import org.pageflow.book.domain.entity.Review;
import org.pageflow.book.port.out.jpa.ReviewPersistencePort;
import org.pageflow.common.permission.ResourcePermissionContext;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 단일 책 리소스에 접근하기 위한 {@link ReviewPermission} 객체를 생성하는 팩토리 클래스
 *
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAccessPermitter {
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

  public void setPermission(UUID reviewId, UID uid) {
    ReviewPermission permission = _grant(reviewId, uid);
    resourcePermissionContext.addResourcePermission(permission);
  }
}