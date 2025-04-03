package org.pageflow.book.port.in.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.ReviewId;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookPermission;
import org.pageflow.book.domain.ReviewPermission;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Review;
import org.pageflow.book.dto.ReviewDto;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.ReviewPersistencePort;
import org.pageflow.common.permission.PermissionRequired;
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
  private final ReviewPersistencePort reviewPersistencePort;
  private final BookPersistencePort bookPersistencePort;
  private final LoadAuthorPort loadAuthorPort;


  @PermissionRequired(
    actions = {"READ"},
    permissionType = BookPermission.class
  )
  public ReviewDto createReview(@BookId("#cmd.bookId") AddReviewCmd cmd) {
    // TODO: 이미 리뷰를 작성한 경우 처리
    Author authorProxy = loadAuthorPort.loadAuthorProxy(cmd.getUid());
    Book bookProxy = _loadBookProxy(cmd.getBookId());
    Review review = Review.create(
      authorProxy,
      bookProxy,
      cmd.getContent(),
      cmd.getScore()
    );

    reviewPersistencePort.persist(review);
    return ReviewDto.from(review);
  }

  @PermissionRequired(
    actions = {"FULL"},
    permissionType = ReviewPermission.class
  )
  public ReviewDto updateReview(@ReviewId("#cmd.reviewId") UpdateReviewCmd cmd) {
    Review review = reviewPersistencePort.findById(cmd.getReviewId()).get();
    review.changeContent(cmd.getContent());
    review.changeScore(cmd.getScore());
    return ReviewDto.from(review);
  }

  @PermissionRequired(
    actions = {"FULL"},
    permissionType = ReviewPermission.class
  )
  public void deleteReview(@ReviewId UUID reviewId) {
    reviewPersistencePort.deleteById(reviewId);
  }


  private Book _loadBookProxy(UUID bookId) {
    return Hibernate.unproxy(bookPersistencePort.getReferenceById(bookId), Book.class);
  }
}
