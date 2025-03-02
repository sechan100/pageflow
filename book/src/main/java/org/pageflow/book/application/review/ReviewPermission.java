package org.pageflow.book.application.review;

import org.pageflow.book.domain.enums.ReviewAction;
import org.pageflow.common.permission.ResourceAction;
import org.pageflow.common.permission.ResourcePermission;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * Review 데이터에 접근하여 데이터를 쓰거나 읽을 수 있는 권한을 나타내는 토큰
 * @author : sechan
 */
public class ReviewPermission implements ResourcePermission<UUID> {
  private final UUID reviewId;
  private final Set<ReviewAction> permittedActions;

  private ReviewPermission(UUID reviewId, ReviewAction... permittedActions) {
    this.reviewId = reviewId;
    this.permittedActions = Set.of(permittedActions);
  }

  public static ReviewPermission writer(UUID reviewId) {
    return new ReviewPermission(reviewId, ReviewAction.values());
  }

  public static ReviewPermission reader(UUID reviewId) {
    return new ReviewPermission(reviewId, ReviewAction.READ);
  }


  @Override
  public UUID getResourceId(){
    return this.reviewId;
  }

  @Override
  public Set<? extends ResourceAction> getPermittedActions() {
    return permittedActions;
  }

  @Override
  public boolean isFullActionPermitted() {
    return permittedActions.containsAll(Arrays.asList(ReviewAction.values()));
  }
}
