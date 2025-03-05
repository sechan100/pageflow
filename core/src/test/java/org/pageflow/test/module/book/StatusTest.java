package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.test.e2e.config.PageflowIntegrationTest;
import org.pageflow.test.e2e.fixture.UserFixture;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.pageflow.test.e2e.shared.TestRes;
import org.pageflow.test.e2e.shared.fixture.Fixture;

import java.util.UUID;

/**
 * 책 상태(BookStatus) 변경 관련 테스트
 * 책은 DRAFT, PUBLISHED, REVISING 세 가지 상태를 가질 수 있음
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class StatusTest {
  private final ApiFactory apiFactory;

  @Test
  @Fixture(UserFixture.class)
  @DisplayName("책 상태 변경 Happy Path 테스트")
  void bookStatusHappyPathTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성 (기본 상태: DRAFT)
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "상태 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // 1. DRAFT -> PUBLISHED (publish)
    TestRes publishRes = userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
    publishRes.isSuccess();

    // 2. PUBLISHED -> REVISING (revise)
    TestRes reviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    reviseRes.isSuccess();

    // 3. REVISING -> PUBLISHED (cancelRevise)
    TestRes cancelReviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=CANCEL_REVISE", null);
    cancelReviseRes.isSuccess();

    // 4. PUBLISHED -> REVISING (다시 revise)
    TestRes reviseAgainRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    reviseAgainRes.isSuccess();

    // 5. REVISING -> PUBLISHED (mergeRevision)
    TestRes mergeRes = userApi.post("/user/books/" + bookId + "/status?cmd=MERGE_REVISION", null);
    mergeRes.isSuccess();
  }

  @Test
  @Fixture(UserFixture.class)
  @DisplayName("Draft 상태에서의 상태 변경 제약 테스트")
  void draftStatusConstraintTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성 (기본 상태: DRAFT)
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "Draft 제약 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // DRAFT 상태에서는 revise 불가능 (출판된 책만 개정 가능)
    TestRes invalidReviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    invalidReviseRes.is(BookCode.INVALID_BOOK_STATUS);

    // DRAFT 상태에서는 cancelRevise 불가능 (개정 중인 책만 개정 취소 가능)
    TestRes invalidCancelRes = userApi.post("/user/books/" + bookId + "/status?cmd=CANCEL_REVISE", null);
    invalidCancelRes.is(BookCode.INVALID_BOOK_STATUS);

    // DRAFT 상태에서는 mergeRevision 불가능 (개정 중인 책만 병합 가능)
    TestRes invalidMergeRes = userApi.post("/user/books/" + bookId + "/status?cmd=MERGE_REVISION", null);
    invalidMergeRes.is(BookCode.INVALID_BOOK_STATUS);

    // DRAFT -> PUBLISHED는 가능
    TestRes publishRes = userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
    publishRes.isSuccess();
  }

  @Test
  @Fixture(UserFixture.class)
  @DisplayName("Published 상태에서의 상태 변경 제약 테스트")
  void publishedStatusConstraintTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성 및 출판
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "Published 제약 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());
    userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null).isSuccess();

    // PUBLISHED 상태에서는 중복 publish 불가능 (이미 출판된 책)
    TestRes duplicatePublishRes = userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
    duplicatePublishRes.is(BookCode.INVALID_BOOK_STATUS);

    // PUBLISHED 상태에서는 cancelRevise 불가능 (개정 중인 책만 개정 취소 가능)
    TestRes invalidCancelRes = userApi.post("/user/books/" + bookId + "/status?cmd=CANCEL_REVISE", null);
    invalidCancelRes.is(BookCode.INVALID_BOOK_STATUS);

    // PUBLISHED 상태에서는 mergeRevision 불가능 (개정 중인 책만 병합 가능)
    TestRes invalidMergeRes = userApi.post("/user/books/" + bookId + "/status?cmd=MERGE_REVISION", null);
    invalidMergeRes.is(BookCode.INVALID_BOOK_STATUS);

    // PUBLISHED -> REVISING은 가능
    TestRes reviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    reviseRes.isSuccess();
  }

  @Test
  @Fixture(UserFixture.class)
  @DisplayName("Revising 상태에서의 상태 변경 제약 테스트")
  void revisingStatusConstraintTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성, 출판, 개정 상태로 변경
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "Revising 제약 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());
    userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null).isSuccess();
    userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null).isSuccess();

    // REVISING 상태에서는 revise 불가능 (이미 개정 중)
    TestRes duplicateReviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    duplicateReviseRes.is(BookCode.INVALID_BOOK_STATUS);

    // REVISING -> PUBLISHED (CancelRevise)는 가능
    TestRes cancelReviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=CANCEL_REVISE", null);
    cancelReviseRes.isSuccess();

    // 다시 REVISING 상태로 변경
    userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null).isSuccess();

    // REVISING -> PUBLISHED (MergeRevision)도 가능
    TestRes mergeRevisionRes = userApi.post("/user/books/" + bookId + "/status?cmd=MERGE_REVISION", null);
    mergeRevisionRes.isSuccess();
  }

  @Test
  @Fixture(UserFixture.class)
  @DisplayName("다른 사용자의 책 상태 변경 권한 테스트")
  void bookStatusPermissionTest() {
    API user1 = apiFactory.user("user1", "user1");
    API user2 = apiFactory.user("user2", "user2");

    // user1이 책 생성
    TestRes createRes = user1.post("/user/books", """
        {
          "title": "권한 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // user2는 user1의 책 상태를 변경할 수 없음 (작가만 상태 변경 가능)
    TestRes unauthorizedPublishRes = user2.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
    unauthorizedPublishRes.is(CommonCode.RESOURCE_PERMISSION_DENIED);

    // user1은 자신의 책 상태 변경 가능
    TestRes publishRes = user1.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
    publishRes.isSuccess();
  }

//  @Test
//  @Fixture(Users.class)
//  @DisplayName("edition 번호 테스트")
//  void editionNumberTest() {
//    API userApi = apiFactory.user("user1", "user1");
//
//    // 책 생성 (기본 상태: DRAFT, edition: 0)
//    TestRes createRes = userApi.post("/user/books", """
//        {
//          "title": "Edition 테스트 도서"
//        }
//      """);
//    createRes.isSuccess();
//    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());
//
//    // DRAFT -> PUBLISHED (publish) -> edition이 1 증가해야 함
//    TestRes publishRes = userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
//    publishRes.isSuccess();
//
//    // PUBLISHED -> REVISING -> PUBLISHED (mergeRevision)
//    // mergeRevision은 edition을 증가시키지 않음
//    userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null).isSuccess();
//    TestRes mergeRes = userApi.post("/user/books/" + bookId + "/status?cmd=MERGE_REVISION", null);
//    mergeRes.isSuccess();
//
//    // PUBLISHED -> REVISING -> PUBLISHED (publish)
//    // 다시 publish는 edition을 증가시킴
//    userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null).isSuccess();
//    TestRes republishRes = userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
//    republishRes.isSuccess();
//  }
}