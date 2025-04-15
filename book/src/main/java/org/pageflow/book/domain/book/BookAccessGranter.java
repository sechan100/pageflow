package org.pageflow.book.domain.book;

import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.book.entity.BookStatus;
import org.pageflow.book.domain.book.entity.BookVisibility;
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
   */
  public Result grant(BookAccess access) {
    return switch(access) {
      case READ -> _grantRead();
      case WRITE -> _grantWrite();
      case AUTHOR -> _grantAuthor();
    };
  }

  /**
   * 책에 대한 읽기 권한을 부여한다.
   */
  private Result _grantRead() {
    if(isAuthor) {
      return Result.SUCCESS();
    } else {
      if(_isGlobalVisibility() && _isPublishedStatus()) {
        return Result.SUCCESS();
      } else {
        return Result.of(BookCode.BOOK_ACCESS_DENIED, "접근 권한이 부족합니다.");
      }
    }
  }

  /**
   * 책에 대한 쓰기 권한을 부여한다.
   */
  private Result _grantWrite() {
    if(isAuthor) {
      if(_isPublishedStatus()) {
        return Result.of(BookCode.BOOK_ACCESS_DENIED, "출판된 책은 수정할 수 없습니다. 수정하려면 개정하세요.");
      } else {
        return Result.SUCCESS();
      }
    } else {
      return Result.of(BookCode.BOOK_ACCESS_DENIED, "접근 권한이 부족합니다.");
    }
  }

  /**
   * 책에 대한 삭제, 상태 변경 등의 권한을 부여한다.
   *
   * @code BOOK_ACCESS_DENIED: 접근 권한이 없는 경우
   */
  private Result _grantAuthor() {
    if(isAuthor) {
      return Result.SUCCESS();
    } else {
      return Result.of(BookCode.BOOK_ACCESS_DENIED, "접근 권한이 부족합니다.");
    }
  }

  // Visibility ======================
  private boolean _isGlobalVisibility() {
    return visibility == BookVisibility.GLOBAL;
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
