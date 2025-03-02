package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.application.review.ReviewId;
import org.pageflow.book.application.review.ReviewPermission;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Review;
import org.pageflow.book.dto.ReviewDto;
import org.pageflow.book.port.in.ReviewUseCase;
import org.pageflow.book.port.in.cmd.AddReviewCmd;
import org.pageflow.book.port.in.cmd.UpdateReviewCmd;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.ReviewPersistencePort;
import org.pageflow.common.permission.ResourceAccessPermissionRequired;
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
public class ReviewService implements ReviewUseCase {
  private final ReviewPersistencePort reviewPersistencePort;
  private final BookPersistencePort bookPersistencePort;
  private final LoadAuthorPort loadAuthorPort;


  @Override
  @ResourceAccessPermissionRequired(permissionType = BookPermission.class)
  public ReviewDto addReview(@BookId("#cmd.bookId") AddReviewCmd cmd) {
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

  @Override
  @ResourceAccessPermissionRequired(permissionType = ReviewPermission.class)
  public ReviewDto updateReview(@ReviewId("#cmd.reviewId") UpdateReviewCmd cmd) {
    Review review = reviewPersistencePort.findById(cmd.getReviewId()).get();
    review.changeContent(cmd.getContent());
    review.changeScore(cmd.getScore());
    return ReviewDto.from(review);
  }

  @Override
  @ResourceAccessPermissionRequired(permissionType = ReviewPermission.class)
  public void removeReview(@ReviewId UUID reviewId) {
    reviewPersistencePort.deleteById(reviewId);
  }


  private Book _loadBookProxy(UUID bookId) {
    return Hibernate.unproxy(bookPersistencePort.getReferenceById(bookId), Book.class);
  }
}
