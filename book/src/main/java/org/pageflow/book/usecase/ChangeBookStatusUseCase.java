package org.pageflow.book.usecase;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.StatusChangeableBook;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.book.entity.BookStatus;
import org.pageflow.book.domain.book.entity.BookVisibility;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.LoadEditableTocNodePort;
import org.pageflow.book.persistence.toc.ReadTocNodePort;
import org.pageflow.book.persistence.toc.TocPersistencePort;
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
public class ChangeBookStatusUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final ReadTocNodePort readTocNodePort;
  private final LoadEditableTocNodePort loadEditableTocNodePort;
  private final TocPersistencePort tocPersistencePort;

  /**
   * 책을 출판한다.
   * {@link BookStatus#DRAFT}
   * 또는 {@link BookStatus#REVISING}에서 사용 가능하다.
   * edition을 1 증가시킨다.
   *
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 이미 발행된 책인 경우
   */
  public Result<BookDto> publish(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 작가 권한 검사
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }

    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    Result publishRes = statusChangeableBook.publish();
    if(publishRes.isFailure()) {
      return publishRes;
    }
    // Toc 병합
    _mergeToReadOnly(book);
    return Result.ok(new BookDto(book));
  }

  /**
   * 책을 개정상태로 변경한다.
   * {@link BookStatus#PUBLISHED}일 때 사용 가능하며,
   * 기존의 출판상태인 책은 독자들에게 여전히 유효하다.
   *
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 출판된 책이 아닌 경우
   */
  public Result<BookDto> startRevision(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    // 작가 권한 검사 ================================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }
    // 상태 변경 ====================================
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    Result startRevisionRes = statusChangeableBook.startRevision();
    if(startRevisionRes.isFailure()) {
      return startRevisionRes;
    }
    // Toc 복제 ====================================
    Preconditions.checkState(tocPersistencePort.existsReadOnlyToc(book), "readOnly toc가 존재해야합니다.");
    Preconditions.checkState(!tocPersistencePort.existsEditableToc(book), "editable toc가 존재하면 안됩니다.");

    Toc readOnlyToc = tocPersistencePort.loadReadonlyToc(book);
    Toc copiedToc = tocPersistencePort.copyReadonlyTocToEditableToc(readOnlyToc);
    return Result.ok(new BookDto(book));
  }

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   * {@link BookStatus#REVISING}일 때 사용 가능하다.
   *
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 개정 중인 책이 아닌 경우
   */
  public Result<BookDto> cancelRevision(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 작가 권한 검사
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }

    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    Result cancelRevisionRes = statusChangeableBook.cancelRevision();
    if(cancelRevisionRes.isFailure()) {
      return cancelRevisionRes;
    }

    // editableToc 삭제
    Preconditions.checkState(
      tocPersistencePort.existsEditableToc(book),
      "editableToc를 삭제하고 readOnlyToc를 남기려면 editableToc가 필요합니다."
    );
    Preconditions.checkState(
      tocPersistencePort.existsReadOnlyToc(book),
      "editableToc를 삭제하고 readOnlyToc를 남기려면 readOnlyToc가 존재해야합니다."
    );
    Toc editableToc = tocPersistencePort.loadEditableToc(book);
    tocPersistencePort.deleteToc(editableToc);

    return Result.ok(new BookDto(book));
  }

  /**
   * 개정을 병합하여 출판상태로 변경한다.
   * {@link BookStatus#REVISING}일 때 사용 가능하다.
   * edition을 올리지 않는다.
   *
   * @apiNote 해당 함수는 edition을 증가시키지 않음으로 간단한 오탈자 수정등의 변경에 용의하다.
   * 책의 내용에 주요한 변경이 있는 경우 사용자는 {@link #publish}를 이용하여 책을 재출판 및 개정하는 것이 좋다.
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 개정 중인 책이 아닌 경우
   */
  public Result<BookDto> mergeRevision(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 작가 권한 검사 ====================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }

    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    Result mergeRevisionRes = statusChangeableBook.mergeRevision();
    if(mergeRevisionRes.isFailure()) {
      return mergeRevisionRes;
    }

    // toc 병합
    _mergeToReadOnly(book);
    return Result.ok(new BookDto(book));
  }

  /**
   * @code BOOK_INVALID_STATUS: DRAFT 상태인 경우
   */
  public Result<BookDto> changeVisibility(UID uid, UUID bookId, BookVisibility visibility) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 작가 권한 검사 ====================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }

    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    Result result = statusChangeableBook.changeVisibility(visibility);
    if(result.isFailure()) {
      return result;
    }
    return Result.ok(new BookDto(book));
  }

  /**
   * book의 toc를 readOnly로 병합한다.
   * editable을 readOnly로 변경하고 기존 readOnly가 존재한다면 삭제.
   */
  private Result<Toc> _mergeToReadOnly(Book book) {
    Preconditions.checkState(
      tocPersistencePort.existsEditableToc(book),
      "toc를 병합하려면 editableToc는 반드시 필요합니다. readOnlyToc는 optional"
    );
    // readOnlyToc가 존재한다면 삭제
    if(tocPersistencePort.existsReadOnlyToc(book)) {
      Toc readOnlyToc = tocPersistencePort.loadReadonlyToc(book);
      tocPersistencePort.deleteToc(readOnlyToc);
    }
    Toc editableToc = tocPersistencePort.loadEditableToc(book);
    Toc resultReadonlyToc = tocPersistencePort.makeTocReadonly(editableToc);
    return Result.ok(resultReadonlyToc);
  }
}
