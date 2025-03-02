package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.ShelfItem;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.port.in.BookShelfUseCase;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.ShelfItemPersistencePort;
import org.pageflow.common.permission.ResourceAccessPermissionRequired;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookShelfService implements BookShelfUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final ShelfItemPersistencePort shelfItemPersistencePort;
  private final LoadAuthorPort loadAuthorPort;


  @Override
  @ResourceAccessPermissionRequired(permissionType = BookPermission.class)
  public BookDto addBookToShelf(@BookId UUID bookId, UID shlefOwnerId) {
    Book book = bookPersistencePort.findById(bookId).get();
    Author shelfOwner = loadAuthorPort.loadAuthorProxy(shlefOwnerId);
    ShelfItem shelfItem = ShelfItem.create(book, shelfOwner);
    shelfItemPersistencePort.persist(shelfItem);
    return BookDto.from(book);
  }

  @Override
  @ResourceAccessPermissionRequired(permissionType = BookPermission.class)
  public void removeBookFromShelf(@BookId UUID bookId, UID shlefOwnerId) {
    shelfItemPersistencePort.deleteByBookIdAndShelfOwnerId(bookId, shlefOwnerId.getValue());
  }

  @Override
  public List<BookDtoWithAuthor> getShelfBooks(UID shlefOwnerId) {
    List<ShelfItem> shelfItems = shelfItemPersistencePort.findBooksByShelfOwnerId(shlefOwnerId.getValue());
    return shelfItems.stream()
      .map(shelfItem -> BookDtoWithAuthor.from(shelfItem.getBook()))
      .toList();
  }
}
