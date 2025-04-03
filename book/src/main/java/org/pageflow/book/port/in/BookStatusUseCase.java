package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.domain.BookPermission;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
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
public class BookStatusUseCase {
  private final BookPersistencePort bookPersistencePort;

  /**
   * 책을 출판한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#DRAFT}
   * 또는 {@link org.pageflow.book.domain.enums.BookStatus#REVISING}에서 사용 가능하다.
   * <p>
   * edition을 1 증가시킨다.
   *
   * @return
   */
  @PermissionRequired(
    actions = {"UPDATE_STATUS"},
    permissionType = BookPermission.class
  )
  public BookDto publish(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.publish();
    return BookDto.from(book);
  }

  /**
   * 책을 개정상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#PUBLISHED}일 때 사용 가능하며,
   * 기존의 출판상태인 책은 독자들에게 여전히 유효하다.
   *
   * @return
   */
  @PermissionRequired(
    actions = {"UPDATE_STATUS"},
    permissionType = BookPermission.class
  )
  public BookDto startRevise(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.revise();
    return BookDto.from(book);
  }

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#REVISING}일 때 사용 가능하다.
   *
   * @return
   */
  @PermissionRequired(
    actions = {"UPDATE_STATUS"},
    permissionType = BookPermission.class
  )
  public BookDto cancelRevise(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.cancelRevise();
    return BookDto.from(book);
  }

  /**
   * 개정을 병합하여 출판상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#REVISING}일 때 사용 가능하다.
   * edition을 올리지 않는다.
   *
   * @return
   * @apiNote 해당 함수는 edition을 증가시키지 않음으로 간단한 오탈자 수정등의 변경에 용의하다.
   * 책의 내용에 주요한 변경이 있는 경우 사용자는 {@link #publish}를 이용하여 책을 재출판 및 개정하는 것이 좋다.
   */
  @PermissionRequired(
    actions = {"UPDATE_STATUS"},
    permissionType = BookPermission.class
  )
  public BookDto revise(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.mergeRevision();
    return BookDto.from(book);
  }

}
