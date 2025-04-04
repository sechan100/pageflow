package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.book.port.in.BookStatusUseCase;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PageflowTest
@RequiredArgsConstructor
public class StatusTest {
  private final BookStatusUseCase bookStatusUseCase;
  private final UserUtils userUtils;
  private final BookUtils bookUtils;

  @Test
  @DisplayName("책 상태 변경 Happy Path 테스트")
  void bookStatusHappyPathTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성 (기본 상태: DRAFT)
    BookDto book = bookUtils.createBook(user, "상태 테스트 도서");

    // 1. 책 처음 publish (DRAFT -> PUBLISHED)
    Result<BookDto> publishRes = bookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(publishRes.isSuccess());

    // 2. 책 개정 시작 (PUBLISHED -> REVISING)
    Result<BookDto> reviseRes = bookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(reviseRes.isSuccess());

    // 3. 책 개정 취소 (REVISING -> PUBLISHED)
    Result<BookDto> cancelReviseRes = bookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(cancelReviseRes.isSuccess());
    assertEquals(1, cancelReviseRes.getSuccessData().getEdition());

    // 4. 다시 개정을 시작하고 visibility를 숨김 (PUBLISHED -> REVISING)
    Result<BookDto> reviseAgainRes = bookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(reviseAgainRes.isSuccess());
    Result<BookDto> changeVisibilityResult = bookStatusUseCase.changeVisibility(user.getUid(), book.getId(), BookVisibility.PERSONAL);
    assertTrue(changeVisibilityResult.isSuccess());
    assertEquals(BookVisibility.PERSONAL, changeVisibilityResult.getSuccessData().getVisibility());

    // 5. 책 개정 완료 및 재출판 (REVISING -> PUBLISHED)
    Result<BookDto> publishAgainRes = bookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(publishAgainRes.isSuccess());
    assertEquals(2, publishAgainRes.getSuccessData().getEdition());
    // 출판하면 자동으로 GLOBAL로 변경됨
    assertEquals(BookVisibility.GLOBAL, publishAgainRes.getSuccessData().getVisibility());

    // 6. 또 다시 개정 시작 (REVISING -> PUBLISHED)
    Result<BookDto> startReviseRes = bookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(startReviseRes.isSuccess());

    // 7. 개정본을 병합 (REVISING -> PUBLISHED)
    Result<BookDto> mergeRes = bookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(mergeRes.isSuccess());
    // 병합은 edition을 올리지 않음
    assertEquals(2, mergeRes.getSuccessData().getEdition());
  }

  @Test
  @DisplayName("Draft 상태에서의 상태 변경 제약 테스트")
  void draftStatusConstraintTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성 (기본 상태: DRAFT)
    BookDto book = bookUtils.createBook(user, "Draft 제약 테스트 도서");

    // DRAFT 상태에서는 revise 불가능 (출판된 책만 개정 가능)
    Result<BookDto> invalidReviseRes = bookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(invalidReviseRes.is(BookCode.INVALID_BOOK_STATUS));

    // DRAFT 상태에서는 cancelRevise 불가능 (개정 중인 책만 개정 취소 가능)
    Result<BookDto> invalidCancelRes = bookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(invalidCancelRes.is(BookCode.INVALID_BOOK_STATUS));

    // DRAFT 상태에서는 mergeRevision 불가능 (개정 중인 책만 병합 가능)
    Result<BookDto> invalidMergeRes = bookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(invalidMergeRes.is(BookCode.INVALID_BOOK_STATUS));

    // DRAFT -> PUBLISHED는 가능
    Result<BookDto> publishRes = bookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(publishRes.isSuccess());
  }

  @Test
  @DisplayName("Published 상태에서의 상태 변경 제약 테스트")
  void publishedStatusConstraintTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성 및 출판
    BookDto book = bookUtils.createBook(user, "Published 제약 테스트 도서");
    bookStatusUseCase.publish(user.getUid(), book.getId());

    // PUBLISHED 상태에서는 중복 publish 불가능 (이미 출판된 책)
    Result<BookDto> duplicatePublishRes = bookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(duplicatePublishRes.is(BookCode.INVALID_BOOK_STATUS));

    // PUBLISHED 상태에서는 cancelRevise 불가능 (개정 중인 책만 개정 취소 가능)
    Result<BookDto> invalidCancelRes = bookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(invalidCancelRes.is(BookCode.INVALID_BOOK_STATUS));

    // PUBLISHED 상태에서는 mergeRevision 불가능 (개정 중인 책만 병합 가능)
    Result<BookDto> invalidMergeRes = bookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(invalidMergeRes.is(BookCode.INVALID_BOOK_STATUS));

    // PUBLISHED -> REVISING은 가능
    Result<BookDto> reviseRes = bookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(reviseRes.isSuccess());
  }

  @Test
  @DisplayName("Revising 상태에서의 상태 변경 제약 테스트")
  void revisingStatusConstraintTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성, 출판, 개정 상태로 변경
    BookDto book = bookUtils.createBook(user, "Revising 제약 테스트 도서");
    bookStatusUseCase.publish(user.getUid(), book.getId());
    bookStatusUseCase.startRevision(user.getUid(), book.getId());

    // REVISING 상태에서는 revise 불가능 (이미 개정 중)
    Result<BookDto> duplicateReviseRes = bookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(duplicateReviseRes.is(BookCode.INVALID_BOOK_STATUS));

    // REVISING -> PUBLISHED (CancelRevise)는 가능
    Result<BookDto> cancelReviseRes = bookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(cancelReviseRes.isSuccess());

    // 다시 REVISING 상태로 변경
    bookStatusUseCase.startRevision(user.getUid(), book.getId());

    // REVISING -> PUBLISHED (MergeRevision)도 가능
    Result<BookDto> mergeRevisionRes = bookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(mergeRevisionRes.isSuccess());
  }

  @Test
  @DisplayName("다른 사용자의 책 상태 변경 권한 테스트")
  void bookStatusPermissionTest() {
    // 사용자 생성
    UserDto user1 = userUtils.createUser("user1");
    UserDto user2 = userUtils.createUser("user2");

    // user1이 책 생성
    BookDto book = bookUtils.createBook(user1, "권한 테스트 도서");

    // user2는 user1의 책 상태를 변경할 수 없음 (작가만 상태 변경 가능)
    Result changeStatusResult = bookStatusUseCase.publish(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = bookStatusUseCase.startRevision(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = bookStatusUseCase.cancelRevision(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = bookStatusUseCase.mergeRevision(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = bookStatusUseCase.changeVisibility(user2.getUid(), book.getId(), BookVisibility.PERSONAL);
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));

    // user1은 자신의 책 상태 변경 가능
    Result<BookDto> publishRes = bookStatusUseCase.publish(user1.getUid(), book.getId());
    assertTrue(publishRes.isSuccess());
  }
}