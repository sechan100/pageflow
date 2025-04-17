package org.pageflow.book.usecase;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.service.GrantedBookLoader;
import org.pageflow.book.domain.book.StatusChangeableBook;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.book.entity.BookStatus;
import org.pageflow.book.domain.book.entity.BookVisibility;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.persistence.toc.TocRepository;
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
  private final GrantedBookLoader grantedBookLoader;
  private final TocRepository tocRepository;

  /**
   * 책을 출판한다.
   * {@link BookStatus#DRAFT}
   * 또는 {@link BookStatus#REVISING}에서 사용 가능하다.
   * edition을 1 증가시킨다.
   *
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 이미 발행된 책인 경우
   */
  public BookDto publish(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.AUTHOR);
    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    statusChangeableBook.publish();
    // Toc 병합
    Preconditions.checkState(tocRepository.existsEditableToc(book), "책을 출판하려면 editableToc는 반드시 필요합니다. readOnlyToc는 존재하는 경우 덮어씌워집니다.");
    // 1. readOnlyToc가 존재하는 경우 삭제
    if(tocRepository.existsReadOnlyToc(book)) {
      Toc readOnlyToc = tocRepository.loadReadonlyToc(book);
      tocRepository.deleteToc(readOnlyToc);
    }
    // 2. editableToc를 readOnlyToc로 복사
    Toc editableToc = tocRepository.loadEditableToc(book);
    tocRepository.copyFromEditableToReadOnly(editableToc);
    // 3. 기존 editableToc 삭제
    tocRepository.deleteToc(editableToc);
    return new BookDto(book);
  }

  /**
   * 책을 개정상태로 변경한다.
   * {@link BookStatus#PUBLISHED}일 때 사용 가능하며,
   * 기존의 출판상태인 책은 독자들에게 여전히 유효하다.
   *
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 출판된 책이 아닌 경우
   */
  public BookDto startRevision(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.AUTHOR);
    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    statusChangeableBook.startRevision();
    // Toc 복사
    Preconditions.checkState(tocRepository.existsReadOnlyToc(book), "readOnly toc가 존재해야합니다.");
    Preconditions.checkState(!tocRepository.existsEditableToc(book), "editable toc가 존재하면 안됩니다.");
    Toc readOnlyToc = tocRepository.loadReadonlyToc(book);
    tocRepository.copyFromReadonlyToEditable(readOnlyToc);
    return new BookDto(book);
  }

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   * {@link BookStatus#REVISING}일 때 사용 가능하다.
   *
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 개정 중인 책이 아닌 경우
   */
  public BookDto cancelRevision(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.AUTHOR);
    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    statusChangeableBook.cancelRevision();
    // editableToc 삭제
    String errorMessage = "개정취소를 위해서는 editable, readOnly toc가 모두 존재해야합니다.";
    Preconditions.checkState(tocRepository.existsEditableToc(book), errorMessage);
    Preconditions.checkState(tocRepository.existsReadOnlyToc(book), errorMessage);
    Toc editableToc = tocRepository.loadEditableToc(book);
    tocRepository.deleteToc(editableToc);
    return new BookDto(book);
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
  public BookDto mergeRevision(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.AUTHOR);
    // 상태 변경
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    statusChangeableBook.mergeRevision();
    // toc 병합
    Preconditions.checkState(tocRepository.existsEditableToc(book), "개정을 병합하기 위해서는 editable toc가 존재해야합니다.");
    Preconditions.checkState(tocRepository.existsReadOnlyToc(book), "개정을 병합하기 위해서는 readOnly toc가 존재해야합니다.");
    // 1. 기존 readOnlyToc 삭제
    Toc readOnlyToc = tocRepository.loadReadonlyToc(book);
    tocRepository.deleteToc(readOnlyToc);
    // 2. editableToc를 readOnlyToc로 복사
    Toc editableToc = tocRepository.loadEditableToc(book);
    tocRepository.copyFromEditableToReadOnly(editableToc);
    // 3. 기존 editableToc 삭제
    tocRepository.deleteToc(editableToc);
    return new BookDto(book);
  }

  /**
   * @code BOOK_INVALID_STATUS: DRAFT 상태인 경우
   */
  public BookDto changeVisibility(UID uid, UUID bookId, BookVisibility visibility) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.AUTHOR);
    StatusChangeableBook statusChangeableBook = new StatusChangeableBook(book);
    statusChangeableBook.changeVisibility(visibility);
    return new BookDto(book);
  }
}
