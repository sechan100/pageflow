package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.CreateReviewReq;
import org.pageflow.book.adapter.in.form.UpdateReviewReq;
import org.pageflow.book.dto.ReviewDto;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.review.AddReviewCmd;
import org.pageflow.book.port.in.review.ReviewAccessPermitter;
import org.pageflow.book.port.in.review.ReviewUseCase;
import org.pageflow.book.port.in.review.UpdateReviewCmd;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RequestMapping("/user/books/{bookId}/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewWebAdapter {
  private final ReviewUseCase reviewUseCase;
  private final RequestContext rqcxt;
  private final BookAccessPermitter bookPermitter;
  private final ReviewAccessPermitter reviewPermitter;


  @PostMapping("")
  @Operation(summary = "책에 리뷰를 작성")
  public ReviewDto createReview(
    @PathVariable UUID bookId,
    @RequestBody CreateReviewReq req
  ) {
    UID uid = rqcxt.getUid();
    bookPermitter.setPermission(bookId, uid);
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
    reviewPermitter.setPermission(reviewId, uid);
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
    reviewPermitter.setPermission(reviewId, uid);
    reviewUseCase.deleteReview(reviewId);
  }

}
