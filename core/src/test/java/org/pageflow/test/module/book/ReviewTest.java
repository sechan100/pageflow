package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.review.ReviewUseCase;
import org.pageflow.test.shared.PageflowTest;

/**
 * @author : sechan
 */
@PageflowTest
@RequiredArgsConstructor
public class ReviewTest {
  private final ReviewUseCase reviewUseCase;
  private final BookAccessPermitter bookAccessPermitter;

  @Test
  @DisplayName("책 리뷰 기능 테스트")
  void reviewTest() {
//    UserDto user1 = dataCreator.createUser("user1");
//    UserDto user2 = dataCreator.createUser("user2");
//    BookDto book = dataCreator.createBook(user1, "리뷰 테스트 도서");
//
//    // user2가 user1의 책에 리뷰를 작성 =======================================
//    AddReviewCmd addReviewCmd = AddReviewCmd.of(
//      user2.getUid(),
//      book.getId(),
//      "좋은 책입니다!",
//      5
//    );
//    // 출판된 책에만 리뷰를 작성할 수 있음
//    bookAccessPermitter.setPermission(book.getId(), user2.getUid());
//    ReviewDto user2_review1 = reviewUseCase.createReview(addReviewCmd);
//    TestRes prePublishReviewRes = createReview.get();
//    prePublishReviewRes.is(CommonCode.RESOURCE_PERMISSION_DENIED, "출판되지 않은 책에 접근할 수 없음");
//    // 책 출판 (리뷰를 위해 PUBLISHED 상태로 변경)
//    user1.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null).isSuccess();
//    TestRes reviewRes = createReview.get();
//    reviewRes.isSuccess();
//
//    UUID reviewId = UUID.fromString(reviewRes.getData().get("id").asText());
//
//    // 리뷰 수정
//    TestRes updateReviewRes = user2.post("/user/books/" + bookId + "/reviews/" + reviewId, """
//        {
//          "content": "매우 좋은 책입니다!",
//          "score": 4
//        }
//      """);
//    updateReviewRes.isSuccess();
//    Assertions.assertEquals("매우 좋은 책입니다!", updateReviewRes.getData().get("content").asText());
//    Assertions.assertEquals(4, updateReviewRes.getData().get("score").asInt());
//
//    // 리뷰 삭제
//    TestRes deleteReviewRes = user2.delete("/user/books/" + bookId + "/reviews/" + reviewId);
//    deleteReviewRes.isSuccess();
  }
}
