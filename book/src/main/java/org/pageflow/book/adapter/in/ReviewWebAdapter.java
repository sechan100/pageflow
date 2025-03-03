package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.CreateReviewReq;
import org.pageflow.book.adapter.in.request.UpdateReviewReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.review.ReviewPermission;
import org.pageflow.book.dto.ReviewDto;
import org.pageflow.book.port.in.cmd.AddReviewCmd;
import org.pageflow.book.port.in.cmd.UpdateReviewCmd;
import org.pageflow.book.port.in.review.ReviewAccessPermitter;
import org.pageflow.book.port.in.review.ReviewUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.permission.ResourcePermissionAware;
import org.pageflow.common.user.UID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RequestMapping("/user/books/{bookId}/review")
@RestController
@RequiredArgsConstructor
public class ReviewWebAdapter {
  private final ReviewUseCase reviewUseCase;
  private final RequestContext rqcxt;
  private final ReviewAccessPermitter permitter;
  private final ResourcePermissionAware permissionAware;


  @PostMapping("")
  @Operation(summary = "책에 리뷰를 작성")
  @SetBookPermission
  public ReviewDto createReview(
    @BookId @PathVariable UUID bookId,
    @RequestBody CreateReviewReq req
    ) {
    UID uid = rqcxt.getUid();
    AddReviewCmd cmd = AddReviewCmd.of(
      uid,
      bookId,
      req.getContent(),
      req.getScore()
    );
    return reviewUseCase.createReview(cmd);
  }


  @PostMapping("/{reviewId}")
  @Operation(summary = "리뷰 수정")
  public ReviewDto updateReview(
    @PathVariable UUID bookId,
    @PathVariable UUID reviewId,
    @RequestBody UpdateReviewReq req
  ) {
    UID uid = rqcxt.getUid();
    ReviewPermission permission = permitter.grant(bookId, uid);
    permissionAware.addResourcePermission(permission);
    UpdateReviewCmd cmd = UpdateReviewCmd.of(
      reviewId,
      req.getContent(),
      req.getScore()
    );
    return reviewUseCase.updateReview(cmd);
  }



  @DeleteMapping("/{reviewId}")
  @Operation(summary = "리뷰 삭제")
  public void deleteReview(
    @PathVariable UUID bookId,
    @PathVariable UUID reviewId
  ) {
    UID uid = rqcxt.getUid();
    ReviewPermission permission = permitter.grant(bookId, uid);
    permissionAware.addResourcePermission(permission);
    reviewUseCase.deleteReview(reviewId);
  }

}
