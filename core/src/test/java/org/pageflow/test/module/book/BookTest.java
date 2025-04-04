package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.BookDtoWithAuthor;
import org.pageflow.book.application.dto.MyBooks;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PageflowTest
@RequiredArgsConstructor
public class BookTest {
  private final UserUtils userUtils;
  private final BookUseCase bookUseCase;

  @Test
  @DisplayName("책 생성, 읽기, 삭제 및 권한 테스트")
  void bookCrdTest() {
    // 1. 사용자 생성
    UserDto user1 = userUtils.createUser("user1");

    // 2. 책 생성
    Result<BookDto> createResult = bookUseCase.createBook(user1.getUid(), "test book 1", null);
    assertTrue(createResult.isSuccess());
    BookDto book = createResult.getSuccessData();

    // 3. 책 조회
    Result<BookDtoWithAuthor> readResult = bookUseCase.readBook(user1.getUid(), new BookId(book.getId()));
    assertTrue(readResult.isSuccess());
    assertEquals(book.getId(), readResult.getSuccessData().getId());

    // 4. 내 책장 조회
    MyBooks myBooks = bookUseCase.queryMyBooks(user1.getUid());
    assertEquals(1, myBooks.getBooks().size());
    assertEquals(book.getId(), myBooks.getBooks().get(0).getId());

    // 5. 다른 사용자 생성
    UserDto user2 = userUtils.createUser("user2");

    // 6. 다른 사용자가 책 삭제 시도 (권한 없음)
    Result deleteResultUnauthorized = bookUseCase.deleteBook(user2.getUid(), new BookId(book.getId()));
    assertTrue(deleteResultUnauthorized.is(BookCode.BOOK_ACCESS_DENIED));

    // 7. 삭제 시도 후에도 책이 여전히 존재하는지 확인
    MyBooks myBooksAfterUnauthorizedDelete = bookUseCase.queryMyBooks(user1.getUid());
    assertEquals(1, myBooksAfterUnauthorizedDelete.getBooks().size());
    assertEquals(book.getId(), myBooksAfterUnauthorizedDelete.getBooks().get(0).getId());

    // 8. 원래 사용자가 책 삭제
    Result deleteResult = bookUseCase.deleteBook(user1.getUid(), new BookId(book.getId()));
    assertTrue(deleteResult.isSuccess());

    // 9. 삭제 확인
    MyBooks myBooksAfterDelete = bookUseCase.queryMyBooks(user1.getUid());
    assertTrue(myBooksAfterDelete.getBooks().isEmpty());
  }
}