package org.pageflow.book.domain.review;

import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.review.constants.ReviewAccess;
import org.pageflow.book.domain.review.entity.Review;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
public class ReviewAccessGranter {
  private final boolean isReviewWriter;
  private final BookAccessGranter bookAccessGranter;
  private final Review review;

  public ReviewAccessGranter(UID uid, Review review) {
    this.isReviewWriter = review.isWriter(uid);
    this.bookAccessGranter = new BookAccessGranter(uid, review.getBook());
    this.review = review;
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책에 대한접근 권한이 없는 경우
   * @code REVIEW_ACCESS_DENIED: 리뷰 작성 권한이 없는 경우
   */
  public Result grant(ReviewAccess access) {
    return switch(access) {
      case WRITE -> _grantWrite();
    };
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책에 대한접근 권한이 없는 경우
   * @code REVIEW_ACCESS_DENIED: 리뷰 작성 권한이 없는 경우
   */
  private Result _grantWrite() {
    if(!isReviewWriter) {
      return Result.of(BookCode.REVIEW_ACCESS_DENIED);
    }

    Result bookAccess = bookAccessGranter.grant(BookAccess.READ);
    if(bookAccess.isFailure()) {
      return bookAccess;
    } else {
      return Result.SUCCESS();
    }
  }

}
