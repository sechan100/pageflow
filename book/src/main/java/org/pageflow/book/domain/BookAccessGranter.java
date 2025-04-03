package org.pageflow.book.domain;

import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
public class BookAccessGranter {
  private final boolean isAuthor;
  private final BookStatus status;
  private final BookVisibility visibility;

  public BookAccessGranter(UID uid, Book book) {
    this.isAuthor = book.getAuthor().getUid().equals(uid);
    this.status = book.getStatus();
    this.visibility = book.getVisibility();
  }

  /**
   * @code BOOK_ACCESS_DENIED: 접근 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 접근권한을 부여할 수 없는 상태인 경우
   */
  public Result grant(BookAccess access) {
    return switch(access) {
      case READ -> _grantRead();
      case WRITE -> _grantWrite();
      case UPDATE -> _grantUpdate();
    };
  }

  /**
   * 책에 대한 읽기 권한을 부여한다.
   *
   * @code BOOK_ACCESS_DENIED: 접근 권한이 없는 경우
   */
  private Result _grantRead() {
    if(isAuthor) {
      return Result.success();
    } else {
      if(_isPublicVisibility() && _isPublishedStatus()) {
        return Result.success();
      } else {
        return Result.of(BookCode.BOOK_ACCESS_DENIED);
      }
    }
  }

  /**
   * 책에 대한 쓰기 권한을 부여한다.
   *
   * @code BOOK_ACCESS_DENIED: 접근 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 책이 출판되어서 수정이 불가능한 경우
   */
  private Result _grantWrite() {
    if(isAuthor) {
      if(_isPublishedStatus()) {
        return Result.of(BookCode.INVALID_BOOK_STATUS, "출판된 책은 수정할 수 없습니다. 수정하려면 개정하세요.");
      } else {
        return Result.success();
      }
    } else {
      return Result.of(BookCode.BOOK_ACCESS_DENIED);
    }
  }

  /**
   * 책에 대한 삭제, 상태 변경 등의 권한을 부여한다.
   *
   * @code BOOK_ACCESS_DENIED: 접근 권한이 없는 경우
   */
  private Result _grantUpdate() {
    if(isAuthor) {
      return Result.success();
    } else {
      return Result.of(BookCode.BOOK_ACCESS_DENIED);
    }
  }

  // Visibility ======================
  private boolean _isPublicVisibility() {
    return visibility == BookVisibility.PUBLIC;
  }

  // Status =======================
  private boolean _isDraftStatus() {
    return status == BookStatus.DRAFT;
  }

  private boolean _isPublishedStatus() {
    return status == BookStatus.PUBLISHED;
  }

  private boolean isRevisingStatus() {
    return status == BookStatus.REVISING;
  }
}
