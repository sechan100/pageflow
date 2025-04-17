package org.pageflow.test.module.book.usecase;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.ReviewDto;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.book.entity.BookVisibility;
import org.pageflow.book.usecase.ChangeBookStatusUseCase;
import org.pageflow.book.usecase.ReviewUseCase;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PageflowTest
@RequiredArgsConstructor
public class ReviewTest {
  private final ReviewUseCase reviewUseCase;
  private final ChangeBookStatusUseCase changeBookStatusUseCase;
  private final UserUtils userUtils;
  private final BookUtils bookUtils;

  @Test
  @DisplayName("책 리뷰 기능 테스트")
  void reviewTest() {
    // 사용자 생성
    UserDto user1 = userUtils.createUser("user1");
    UserDto user2 = userUtils.createUser("user2");

    // 책 생성
    BookDto book = bookUtils.createBook(user1, "리뷰 테스트 도서");

    // 출판 상태로 변경 (리뷰는 출판된 책에만 작성 가능)
    changeBookStatusUseCase.publish(user1.getUid(), book.getId());

    // 리뷰 작성
    Result<ReviewDto> reviewResult = reviewUseCase.createReview(
      user2.getUid(),
      book.getId(),
      "매우 좋은 책입니다!",
      5
    );
    assertTrue(reviewResult.isSuccess());
    ReviewDto review = reviewResult.getSuccessData();

    // 리뷰 수정
    Result<ReviewDto> updateResult = reviewUseCase.updateReview(
      user2.getUid(),
      review.getId(),
      "적당히 좋은 책입니다!",
      2
    );
    assertTrue(updateResult.isSuccess());
    assertEquals("적당히 좋은 책입니다!", updateResult.getSuccessData().getContent());
    assertEquals(2, updateResult.getSuccessData().getScore());

    // 다른 사람이 리뷰 수정 및 삭제 시도
    Result<ReviewDto> updateResult2 = reviewUseCase.updateReview(
      user1.getUid(),
      review.getId(),
      "감히 2점을 줘?",
      5
    );
    assertTrue(updateResult2.is(BookCode.REVIEW_ACCESS_DENIED));
    // 삭제 시도
    Result deleteResult2 = reviewUseCase.deleteReview(user1.getUid(), review.getId());
    assertTrue(deleteResult2.is(BookCode.REVIEW_ACCESS_DENIED));

    // 작가가 책을 PERSONAL visibility로 변경
    changeBookStatusUseCase.changeVisibility(
      user1.getUid(),
      book.getId(),
      BookVisibility.PERSONAL
    );

    // 리뷰 삭제 시도
    Result deleteResult3 = reviewUseCase.deleteReview(user2.getUid(), review.getId());
    assertTrue(deleteResult3.is(BookCode.BOOK_ACCESS_DENIED));

    // 책을 다시 GLOBAL visibility로 변경
    changeBookStatusUseCase.changeVisibility(
      user1.getUid(),
      book.getId(),
      BookVisibility.GLOBAL
    );

    // 실제로 리뷰 삭제
    Result deleteResult = reviewUseCase.deleteReview(user2.getUid(), review.getId());
    assertTrue(deleteResult.isSuccess());
  }
}