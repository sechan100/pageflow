package org.pageflow.book.domain.book;

import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.book.entity.BookStatus;
import org.pageflow.book.domain.book.entity.BookVisibility;
import org.pageflow.book.domain.book.entity.PublishedRecord;
import org.pageflow.common.result.Result;

import java.util.Optional;

/**
 * @author : sechan
 */
public class StatusChangeableBook {
  private final Book book;

  public StatusChangeableBook(Book book) {
    this.book = book;
  }

  /**
   * 책의 visibility를 변경한다.
   * {@link BookStatus#DRAFT}인 경우 변경 할 수 없고, PRIVATE로 강제된다.
   *
   * @code BOOK_INVALID_STATUS: DRAFT 상태인 경우
   */
  public Result changeVisibility(BookVisibility visibility) {
    if(book.getStatus() == BookStatus.DRAFT) {
      return Result.of(
        BookCode.INVALID_BOOK_STATUS,
        "DRAFT 상태의 책은 공개범위를 변경할 수 없습니다."
      );
    }
    book.setVisibility(visibility);
    return Result.SUCCESS();
  }

  /**
   * 책을 출판 상태로 변경하고, PublishedRecord를 하나 생성한다.
   * visibility는 GLOBAL로 설정된다.
   *
   * @code BOOK_INVALID_STATUS: 이미 발행된 책인 경우
   */
  public Result publish() {
    if(book.getStatus() == BookStatus.PUBLISHED) {
      return Result.of(
        BookCode.INVALID_BOOK_STATUS,
        "이미 발행된 책입니다."
      );
    }

    book.setStatus(BookStatus.PUBLISHED);
    book.setVisibility(BookVisibility.GLOBAL);

    Optional<PublishedRecord> latestPublishedRecord = book.getLatestPublishedRecord();
    int newEdition = latestPublishedRecord
      .map(pr -> pr.getEdition() + 1)
      .orElse(1);

    PublishedRecord newPublishRecord = new PublishedRecord(book, newEdition);
    book.getPublishedRecords().add(newPublishRecord);
    return Result.SUCCESS();
  }

  /**
   * 책을 개정중 상태로 변경한다.
   *
   * @code BOOK_INVALID_STATUS: 출판된 책이 아닌 경우
   */
  public Result startRevision() {
    if(book.getStatus() == BookStatus.PUBLISHED) {
      book.setStatus(BookStatus.REVISING);
      return Result.SUCCESS();
    } else {
      return Result.of(
        BookCode.INVALID_BOOK_STATUS,
        "출판된 책만 개정을 시작할 수 있습니다."
      );
    }
  }

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   *
   * @code BOOK_INVALID_STATUS: 개정 중인 책이 아닌 경우
   */
  public Result cancelRevision() {
    if(book.getStatus() == BookStatus.REVISING) {
      book.setStatus(BookStatus.PUBLISHED);
      return Result.SUCCESS();
    } else {
      return Result.of(
        BookCode.INVALID_BOOK_STATUS,
        "개정 중인 책만 개정을 취소할 수 있습니다."
      );
    }
  }

  /**
   * 개정중인 책을 기존 책과 병합하고 출판상태로 변경한다.
   * edition을 올리지 않는다.
   *
   * @code BOOK_INVALID_STATUS: 개정 중인 책이 아닌 경우
   */
  public Result mergeRevision() {
    if(book.getStatus() == BookStatus.REVISING) {
      book.setStatus(BookStatus.PUBLISHED);
      return Result.SUCCESS();
    } else {
      return Result.of(
        BookCode.INVALID_BOOK_STATUS,
        "개정을 병합하려면 개정 중인 책이어야 합니다."
      );
    }
  }
}
