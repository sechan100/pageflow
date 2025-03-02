package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookStatusUseCase;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
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
public class BookStatusService implements BookStatusUseCase {
  private final BookPersistencePort bookPersistencePort;


  @Override
  @ResourceAccessPermissionRequired(actions = { "UPDATE_STATUS" }, permissionType = BookPermission.class)
  public BookDto publish(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.publish();
    return BookDto.from(book);
  }

  @Override
  @ResourceAccessPermissionRequired(actions = { "UPDATE_STATUS" }, permissionType = BookPermission.class)
  public BookDto revise(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.revise();
    return BookDto.from(book);
  }

  @Override
  @ResourceAccessPermissionRequired(actions = { "UPDATE_STATUS" }, permissionType = BookPermission.class)
  public BookDto cancelRevise(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.cancelRevise();
    return BookDto.from(book);
  }

  @Override
  @ResourceAccessPermissionRequired(actions = { "UPDATE_STATUS" }, permissionType = BookPermission.class)
  public BookDto mergeRevision(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.mergeRevision();
    return BookDto.from(book);
  }

}
