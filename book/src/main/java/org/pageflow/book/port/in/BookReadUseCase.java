package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.AuthorDto;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.MyBooks;
import org.pageflow.book.application.dto.PublishedBookDto;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookReadUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final LoadAuthorPort loadAuthorPort;

  /**
   * @code BOOK_ACCESS_DENIED: 책을 읽을 권한이 없는 경우
   */
  public Result<PublishedBookDto> readBook(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findBookWithAuthorById(bookId).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }
    return Result.success(PublishedBookDto.from(book));
  }

  public MyBooks getMyBooks(UID uid) {
    Author author = loadAuthorPort.loadAuthor(uid).get();
    List<Book> books = bookPersistencePort.findBooksByAuthorId(uid.getValue());

    return new MyBooks(
      AuthorDto.from(author),
      books.stream().map(BookDto::from).toList()
    );
  }
}
