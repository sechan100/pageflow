package org.pageflow.book.port.in;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.StatusChangeableBook;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.port.out.EditTocPort;
import org.pageflow.book.port.out.ReadTocPort;
import org.pageflow.book.port.out.TocTreePersistencePort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
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
  private final ReadTocPort readTocPort;
  private final EditTocPort editTocPort;
  private final TocTreePersistencePort tocTreePersistencePort;

  /**
   * 책을 출판한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#DRAFT}
   * 또는 {@link org.pageflow.book.domain.enums.BookStatus#REVISING}에서 사용 가능하다.
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
    _mergeToReadonly(book);
    return Result.success(BookDto.from(book));
  }

  /**
   * 책을 개정상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#PUBLISHED}일 때 사용 가능하며,
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
    Preconditions.checkState(tocTreePersistencePort.existsReadonlyToc(book), "readonly toc가 존재해야합니다.");
    Preconditions.checkState(!tocTreePersistencePort.existsEditableToc(book), "editable toc가 존재하면 안됩니다.");

    Toc readonlyToc = readTocPort.loadReadonlyToc(book);
    Toc copiedToc = tocTreePersistencePort.copyReadonlyTocToEditableToc(readonlyToc);
    return Result.success(BookDto.from(book));
  }

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#REVISING}일 때 사용 가능하다.
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
      tocTreePersistencePort.existsEditableToc(book),
      "editableToc를 삭제하고 readonlyToc를 남기려면 editableToc가 필요합니다."
    );
    Preconditions.checkState(
      tocTreePersistencePort.existsReadonlyToc(book),
      "editableToc를 삭제하고 readonlyToc를 남기려면 readonlyToc가 존재해야합니다."
    );
    Toc editableToc = editTocPort.loadEditableToc(book);
    tocTreePersistencePort.deleteToc(editableToc);

    return Result.success(BookDto.from(book));
  }

  /**
   * 개정을 병합하여 출판상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#REVISING}일 때 사용 가능하다.
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
    _mergeToReadonly(book);
    return Result.success(BookDto.from(book));
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
    return Result.success(BookDto.from(book));
  }

  /**
   * book의 toc를 readonly로 병합한다.
   * editable을 readonly로 변경하고 기존 readonly가 존재한다면 삭제.
   */
  private Result<Toc> _mergeToReadonly(Book book) {
    Preconditions.checkState(
      tocTreePersistencePort.existsEditableToc(book),
      "toc를 병합하려면 editableToc는 반드시 필요합니다. readonlyToc는 optional"
    );
    // readonlyToc가 존재한다면 삭제
    if(tocTreePersistencePort.existsReadonlyToc(book)) {
      Toc readonlyToc = readTocPort.loadReadonlyToc(book);
      tocTreePersistencePort.deleteToc(readonlyToc);
    }
    Toc editableToc = editTocPort.loadEditableToc(book);
    Toc resultReadonlyToc = tocTreePersistencePort.makeTocReadonly(editableToc);
    return Result.success(resultReadonlyToc);
  }
}
