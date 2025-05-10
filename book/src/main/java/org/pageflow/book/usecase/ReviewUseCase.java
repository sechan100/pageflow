package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.pageflow.book.application.dto.ReviewDto;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.review.ReviewAccessGranter;
import org.pageflow.book.domain.review.constants.ReviewAccess;
import org.pageflow.book.domain.review.entity.Review;
import org.pageflow.book.persistence.AuthorRepository;
import org.pageflow.book.persistence.BookRepository;
import org.pageflow.book.persistence.ReviewRepository;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewUseCase {
  private final ReviewRepository reviewRepository;
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;


  /**
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 책이 접근권한을 부여할 수 없는 상태인 경우
   * @code FIELD_VALIDATION_ERROR: score가 1 ~ 5사이의 정수가 아닌 경우
   */
  public Result<ReviewDto> createReview(
    UID uid,
    UUID bookId,
    String content,
    int score
  ) {
    Book book = bookRepository.findById(bookId).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }

    // TODO: 이미 리뷰를 작성한 경우 처리
    Author authorProxy = authorRepository.loadAuthorProxy(uid);
    Book bookProxy = _loadBookProxy(bookId);
    Result<Review> reviewRes = Review.create(
      authorProxy,
      bookProxy,
      content,
      score
    );
    if(reviewRes.isFailure()) return (Result) reviewRes;

    Review review = reviewRepository.save(reviewRes.getSuccessData());
    return Result.ok(ReviewDto.from(review));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책에 대한접근 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 책이 접근권한을 부여할 수 없는 상태인 경우
   * @code REVIEW_ACCESS_DENIED: 리뷰 작성 권한이 없는 경우
   */
  public Result<ReviewDto> updateReview(
    UID uid,
    UUID reviewId,
    String content,
    int score
  ) {
    Review review = reviewRepository.findById(reviewId).get();

    // 권한 검사 ========================
    ReviewAccessGranter accessGranter = new ReviewAccessGranter(uid, review);
    Result access = accessGranter.grant(ReviewAccess.WRITE);
    if(access.isFailure()) {
      return access;
    }

    // 리뷰 수정
    review.changeContent(content);
    review.changeScore(score);
    return Result.ok(ReviewDto.from(review));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책에 대한접근 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 책이 접근권한을 부여할 수 없는 상태인 경우
   * @code REVIEW_ACCESS_DENIED: 리뷰 작성 권한이 없는 경우
   */
  public Result deleteReview(UID uid, UUID reviewId) {
    Review review = reviewRepository.findById(reviewId).get();

    // 권한 검사 ========================
    ReviewAccessGranter accessGranter = new ReviewAccessGranter(uid, review);
    Result access = accessGranter.grant(ReviewAccess.WRITE);
    if(access.isFailure()) {
      return access;
    }

    // 삭제
    reviewRepository.delete(review);
    return Result.ok();
  }


  private Book _loadBookProxy(UUID bookId) {
    return Hibernate.unproxy(bookRepository.getReferenceById(bookId), Book.class);
  }
}
