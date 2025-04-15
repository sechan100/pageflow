package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.book.entity.BookVisibility;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.usecase.ChangeBookStatusUseCase;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PageflowTest
@RequiredArgsConstructor
public class StatusFlowTest {
  private final ChangeBookStatusUseCase changeBookStatusUseCase;
  private final BookPersistencePort bookPersistencePort;
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
    Result<BookDto> publishRes = changeBookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(publishRes.isSuccess());

    // 2. 책 개정 시작 (PUBLISHED -> REVISING)
    Result<BookDto> reviseRes = changeBookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(reviseRes.isSuccess());

    // 3. 책 개정 취소 (REVISING -> PUBLISHED)
    Result<BookDto> cancelReviseRes = changeBookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(cancelReviseRes.isSuccess());
    int newEdition = bookPersistencePort.findById(book.getId()).get().getLatestPublishedRecord().get().getEdition();
    assertEquals(1, newEdition);

    // 4. 다시 개정을 시작하고 visibility를 숨김 (PUBLISHED -> REVISING)
    Result<BookDto> reviseAgainRes = changeBookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(reviseAgainRes.isSuccess());
    Result<BookDto> changeVisibilityResult = changeBookStatusUseCase.changeVisibility(user.getUid(), book.getId(), BookVisibility.PERSONAL);
    assertTrue(changeVisibilityResult.isSuccess());
    assertEquals(BookVisibility.PERSONAL, changeVisibilityResult.get().getVisibility());

    // 5. 책 개정 완료 및 재출판 (REVISING -> PUBLISHED)
    Result<BookDto> publishAgainRes = changeBookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(publishAgainRes.isSuccess());
    int afterPublishEdition = bookPersistencePort.findById(book.getId()).get().getLatestPublishedRecord().get().getEdition();
    assertEquals(2, afterPublishEdition);
    // 출판하면 자동으로 GLOBAL로 변경됨
    assertEquals(BookVisibility.GLOBAL, publishAgainRes.get().getVisibility());

    // 6. 또 다시 개정 시작 (REVISING -> PUBLISHED)
    Result<BookDto> startReviseRes = changeBookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(startReviseRes.isSuccess());

    // 7. 개정본을 병합 (REVISING -> PUBLISHED)
    Result<BookDto> mergeRes = changeBookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(mergeRes.isSuccess());
    // 병합은 edition을 올리지 않음
    int mergeEdition = bookPersistencePort.findById(book.getId()).get().getLatestPublishedRecord().get().getEdition();
    assertEquals(2, mergeEdition);
  }

  @Test
  @DisplayName("Draft 상태에서의 상태 변경 제약 테스트")
  void draftStatusConstraintTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성 (기본 상태: DRAFT)
    BookDto book = bookUtils.createBook(user, "Draft 제약 테스트 도서");

    // DRAFT 상태에서는 revise 불가능 (출판된 책만 개정 가능)
    Result<BookDto> invalidReviseRes = changeBookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(invalidReviseRes.is(BookCode.INVALID_BOOK_STATUS));

    // DRAFT 상태에서는 cancelRevise 불가능 (개정 중인 책만 개정 취소 가능)
    Result<BookDto> invalidCancelRes = changeBookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(invalidCancelRes.is(BookCode.INVALID_BOOK_STATUS));

    // DRAFT 상태에서는 mergeRevision 불가능 (개정 중인 책만 병합 가능)
    Result<BookDto> invalidMergeRes = changeBookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(invalidMergeRes.is(BookCode.INVALID_BOOK_STATUS));

    // DRAFT -> PUBLISHED는 가능
    Result<BookDto> publishRes = changeBookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(publishRes.isSuccess());
  }

  @Test
  @DisplayName("Published 상태에서의 상태 변경 제약 테스트")
  void publishedStatusConstraintTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성 및 출판
    BookDto book = bookUtils.createBook(user, "Published 제약 테스트 도서");
    changeBookStatusUseCase.publish(user.getUid(), book.getId());

    // PUBLISHED 상태에서는 중복 publish 불가능 (이미 출판된 책)
    Result<BookDto> duplicatePublishRes = changeBookStatusUseCase.publish(user.getUid(), book.getId());
    assertTrue(duplicatePublishRes.is(BookCode.INVALID_BOOK_STATUS));

    // PUBLISHED 상태에서는 cancelRevise 불가능 (개정 중인 책만 개정 취소 가능)
    Result<BookDto> invalidCancelRes = changeBookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(invalidCancelRes.is(BookCode.INVALID_BOOK_STATUS));

    // PUBLISHED 상태에서는 mergeRevision 불가능 (개정 중인 책만 병합 가능)
    Result<BookDto> invalidMergeRes = changeBookStatusUseCase.mergeRevision(user.getUid(), book.getId());
    assertTrue(invalidMergeRes.is(BookCode.INVALID_BOOK_STATUS));

    // PUBLISHED -> REVISING은 가능
    Result<BookDto> reviseRes = changeBookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(reviseRes.isSuccess());
  }

  @Test
  @DisplayName("Revising 상태에서의 상태 변경 제약 테스트")
  void revisingStatusConstraintTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성, 출판, 개정 상태로 변경
    BookDto book = bookUtils.createBook(user, "Revising 제약 테스트 도서");
    changeBookStatusUseCase.publish(user.getUid(), book.getId());
    changeBookStatusUseCase.startRevision(user.getUid(), book.getId());

    // REVISING 상태에서는 revise 불가능 (이미 개정 중)
    Result<BookDto> duplicateReviseRes = changeBookStatusUseCase.startRevision(user.getUid(), book.getId());
    assertTrue(duplicateReviseRes.is(BookCode.INVALID_BOOK_STATUS));

    // REVISING -> PUBLISHED (CancelRevise)는 가능
    Result<BookDto> cancelReviseRes = changeBookStatusUseCase.cancelRevision(user.getUid(), book.getId());
    assertTrue(cancelReviseRes.isSuccess());

    // 다시 REVISING 상태로 변경
    changeBookStatusUseCase.startRevision(user.getUid(), book.getId());

    // REVISING -> PUBLISHED (MergeRevision)도 가능
    Result<BookDto> mergeRevisionRes = changeBookStatusUseCase.mergeRevision(user.getUid(), book.getId());
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
    Result changeStatusResult = changeBookStatusUseCase.publish(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = changeBookStatusUseCase.startRevision(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = changeBookStatusUseCase.cancelRevision(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = changeBookStatusUseCase.mergeRevision(user2.getUid(), book.getId());
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));
    changeStatusResult = changeBookStatusUseCase.changeVisibility(user2.getUid(), book.getId(), BookVisibility.PERSONAL);
    assertTrue(changeStatusResult.is(BookCode.BOOK_ACCESS_DENIED));

    // user1은 자신의 책 상태 변경 가능
    Result<BookDto> publishRes = changeBookStatusUseCase.publish(user1.getUid(), book.getId());
    assertTrue(publishRes.isSuccess());
  }
}