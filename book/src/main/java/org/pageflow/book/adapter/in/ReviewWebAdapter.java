package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.ReviewForm;
import org.pageflow.book.application.dto.ReviewDto;
import org.pageflow.book.port.in.ReviewUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@RequestMapping("/user/books/{bookId}/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewWebAdapter {
  private final ReviewUseCase reviewUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "책에 리뷰를 작성")
  public Result<ReviewDto> createReview(
    @PathVariable UUID bookId,
    @Valid @RequestBody ReviewForm.Create form
  ) {
    UID uid = rqcxt.getUid();
    Result<ReviewDto> result = reviewUseCase.createReview(
      uid,
      bookId,
      form.getContent(),
      form.getScore()
    );
    return result;
  }


  @PostMapping("/{reviewId}")
  @Operation(summary = "리뷰 수정")
  public Result<ReviewDto> updateReview(
    @PathVariable UUID bookId,
    @PathVariable UUID reviewId,
    @Valid @RequestBody ReviewForm.Update form
  ) {
    UID uid = rqcxt.getUid();
    Result<ReviewDto> result = reviewUseCase.updateReview(
      uid,
      reviewId,
      form.getContent(),
      form.getScore()
    );
    return result;
  }


  @DeleteMapping("/{reviewId}")
  @Operation(summary = "리뷰 삭제")
  public Result deleteReview(
    @PathVariable UUID bookId,
    @PathVariable UUID reviewId
  ) {
    UID uid = rqcxt.getUid();
    Result result = reviewUseCase.deleteReview(uid, reviewId);
    return result;
  }

}
