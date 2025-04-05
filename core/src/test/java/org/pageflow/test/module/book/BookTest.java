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
    // 사용자 생성
    UserDto user1 = userUtils.createUser("user1");

    // 책 생성
    Result<BookDto> createResult = bookUseCase.createBook(user1.getUid(), "test book 1", null);
    assertTrue(createResult.isSuccess());
    BookDto book = createResult.getSuccessData();

    // 책 조회
    Result<BookDtoWithAuthor> readResult = bookUseCase.readBook(user1.getUid(), book.getId());
    assertTrue(readResult.isSuccess());
    assertEquals(book.getId(), readResult.getSuccessData().getId());

    // 내 책장 조회
    MyBooks myBooks = bookUseCase.queryMyBooks(user1.getUid());
    assertEquals(1, myBooks.getBooks().size());
    assertEquals(book.getId(), myBooks.getBooks().get(0).getId());

    // 다른 사용자 생성
    UserDto user2 = userUtils.createUser("user2");

    // 다른 사용자가 책 제목 변경 시도
    Result changeBookTitleRes = bookUseCase.changeBookTitle(user2.getUid(), book.getId(), "test book 2");
    assertTrue(changeBookTitleRes.is(BookCode.BOOK_ACCESS_DENIED));

    // 책 제목 변경
    String newTitle = "test book 3";
    Result<BookDto> changeBookTitleRes2 = bookUseCase.changeBookTitle(user1.getUid(), book.getId(), newTitle);
    assertTrue(changeBookTitleRes2.isSuccess());
    assertEquals(newTitle, changeBookTitleRes2.getSuccessData().getTitle());

    // 다른 사용자가 책 삭제 시도 (권한 없음)
    Result deleteResultUnauthorized = bookUseCase.deleteBook(user2.getUid(), book.getId());
    assertTrue(deleteResultUnauthorized.is(BookCode.BOOK_ACCESS_DENIED));

    // 삭제 시도 후에도 책이 여전히 존재하는지 확인
    MyBooks myBooksAfterUnauthorizedDelete = bookUseCase.queryMyBooks(user1.getUid());
    assertEquals(1, myBooksAfterUnauthorizedDelete.getBooks().size());
    assertEquals(book.getId(), myBooksAfterUnauthorizedDelete.getBooks().get(0).getId());

    // 원래 사용자가 책 삭제
    Result deleteResult = bookUseCase.deleteBook(user1.getUid(), book.getId());
    assertTrue(deleteResult.isSuccess());

    // 삭제 확인
    MyBooks myBooksAfterDelete = bookUseCase.queryMyBooks(user1.getUid());
    assertTrue(myBooksAfterDelete.getBooks().isEmpty());
  }

  @Test
  @DisplayName("책 설명 변경 테스트")
  void changeBookDescriptionTest() {
    // 사용자 생성
    UserDto user1 = userUtils.createUser("user1");

    // 책 생성
    Result<BookDto> createResult = bookUseCase.createBook(user1.getUid(), "test book 1", null);
    assertTrue(createResult.isSuccess());
    BookDto book = createResult.getSuccessData();

    // 책 설명 변경
    String newDescription = "test book description";
    Result<BookDto> changeBookDescriptionRes = bookUseCase.changeBookDescription(user1.getUid(), book.getId(), newDescription);
    assertTrue(changeBookDescriptionRes.isSuccess());
    assertEquals(newDescription, changeBookDescriptionRes.getSuccessData().getDescription());

    // 다른 사용자가 책 설명 변경 시도
    UserDto user2 = userUtils.createUser("user2");
    Result changeBookDescriptionRes2 = bookUseCase.changeBookDescription(user2.getUid(), book.getId(), "new description");
    assertTrue(changeBookDescriptionRes2.is(BookCode.BOOK_ACCESS_DENIED));
    // 책 설명이 변경되지 않았는지 확인
    Result<BookDtoWithAuthor> readResult = bookUseCase.readBook(user1.getUid(), book.getId());
    assertTrue(readResult.isSuccess());
    assertEquals(newDescription, readResult.getSuccessData().getDescription());
  }

}