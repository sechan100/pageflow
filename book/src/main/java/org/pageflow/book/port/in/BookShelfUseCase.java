package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.dto.book.WithAuthorBookDto;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.ShelfItem;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.ShelfItemPersistencePort;
import org.pageflow.common.result.Result;
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
public class BookShelfUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final ShelfItemPersistencePort shelfItemPersistencePort;
  private final LoadAuthorPort loadAuthorPort;


  /**
   * 책장에 책을 추가한다.
   *
   * @code BOOK_ACCESS_DENIED: 책에 대한 권한이 없는 경우
   */
  public Result<BookDto> addBookToShelf(UID shlefOwnerId, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(shlefOwnerId, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }
    // 책장에 책 넣기
    Author shelfOwner = loadAuthorPort.loadAuthorProxy(shlefOwnerId);
    ShelfItem shelfItem = ShelfItem.create(book, shelfOwner);
    shelfItemPersistencePort.persist(shelfItem);
    BookDto dto = BookDto.from(book);
    return Result.success(dto);
  }

  /**
   * 책장에서 책을 제거한다.
   *
   * @code BOOK_ACCESS_DENIED: 책에 대한 권한이 없는 경우
   */
  public Result removeBookFromShelf(UID shlefOwnerId, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(shlefOwnerId, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }

    // 책장에 책 제거
    shelfItemPersistencePort.deleteByBookIdAndShelfOwnerId(bookId, shlefOwnerId.getValue());
    return Result.success();
  }

  /**
   * 책장에 있는 책들을 조회한다.
   */
  public List<WithAuthorBookDto> getShelfBooks(UID shlefOwnerId) {
    List<ShelfItem> shelfItems = shelfItemPersistencePort.findBooksByShelfOwnerId(shlefOwnerId.getValue());
    return shelfItems.stream()
      .map(shelfItem -> WithAuthorBookDto.from(shelfItem.getBook()))
      .toList();
  }
}
