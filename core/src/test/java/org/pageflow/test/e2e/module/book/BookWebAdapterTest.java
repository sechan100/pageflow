package org.pageflow.test.e2e.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.test.e2e.config.PageflowIntegrationTest;
import org.pageflow.test.e2e.fixture.Users;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.pageflow.test.e2e.shared.TestRes;
import org.pageflow.test.e2e.shared.fixture.Fixture;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class BookWebAdapterTest {
  private final ApiFactory apiFactory;

  @Test
  @Fixture(Users.class)
  @DisplayName("책 CRUD")
  void bookCrudTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // 책 조회
    TestRes getBookRes = userApi.get("/user/books/" + bookId);
    getBookRes.isSuccess();
    assert getBookRes.getData().get("id").asText().equals(bookId.toString());
    Assertions.assertEquals("테스트 도서", getBookRes.getData().get("title").asText());

    // 내 책장을 통해서 책 조회
    TestRes myBooks = userApi.get("/user/books");
    myBooks.isSuccess();
    Assertions.assertEquals(bookId.toString(), myBooks.getData().get("books").get(0).get("id").asText());

    // 책 삭제
    TestRes deleteRes = userApi.delete("/user/books/" + bookId);
    deleteRes.isSuccess();
    
    // 책장 재조회하여 삭제 확인
    TestRes myBooks2 = userApi.get("/user/books");
    myBooks2.isSuccess();
    Assertions.assertTrue(myBooks2.getData().get("books").isEmpty());
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("책 상태 변경 흐름 테스트")
  void bookStatusTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성 (기본 상태: DRAFT)
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "상태 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // DRAFT -> PUBLISHED (publish)
    TestRes publishRes = userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);
    publishRes.isSuccess();
    
    // 책 상태 확인
    TestRes bookAfterPublish = userApi.get("/user/books/" + bookId);
    bookAfterPublish.isSuccess();
    
    // PUBLISHED -> REVISING (revise)
    TestRes reviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    reviseRes.isSuccess();

    
    // REVISING -> PUBLISHED (cancelRevise)
    TestRes cancelReviseRes = userApi.post("/user/books/" + bookId + "/status?cmd=CANCEL_REVISE", null);
    cancelReviseRes.isSuccess();
    
    // PUBLISHED -> REVISING (다시 revise)
    TestRes reviseAgainRes = userApi.post("/user/books/" + bookId + "/status?cmd=REVISE", null);
    reviseAgainRes.isSuccess();
    
    // REVISING -> PUBLISHED (mergeRevision)
    TestRes mergeRes = userApi.post("/user/books/" + bookId + "/status?cmd=MERGE_REVISION", null);
    mergeRes.isSuccess();
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("목차(TOC) 관련 테스트")
  void tocTest() {
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "목차 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // 목차 조회 (루트 폴더가 있어야 함)
    TestRes tocRes = userApi.get("/user/books/" + bookId + "/toc");
    tocRes.isSuccess();
    UUID rootFolderId = UUID.fromString(tocRes.getData().get("root").get("id").asText());

    // 폴더 생성
    TestRes folderRes = userApi.post("/user/books/" + bookId + "/toc/folders", """
        {
          "parentNodeId": "%s",
          "title": "1장"
        }
      """.formatted(rootFolderId));
    folderRes.isSuccess();
    UUID folderId = UUID.fromString(folderRes.getData().get("id").asText());

    // 섹션 생성
    TestRes sectionRes = userApi.post("/user/books/" + bookId + "/toc/sections", """
        {
          "parentNodeId": "%s",
          "title": "1.1 섹션"
        }
      """.formatted(folderId));
    sectionRes.isSuccess();
    UUID sectionId = UUID.fromString(sectionRes.getData().get("id").asText());

    // 섹션 내용 조회
    TestRes sectionContentRes = userApi.get("/user/books/" + bookId + "/toc/sections/" + sectionId + "/content");
    sectionContentRes.isSuccess();
    
    // 섹션 업데이트
    TestRes updateSectionRes = userApi.post("/user/books/" + bookId + "/toc/sections/" + sectionId, """
        {
          "title": "1.1 수정된 섹션",
          "content": "섹션 내용입니다."
        }
      """);
    updateSectionRes.isSuccess();
    assert updateSectionRes.getData().get("title").asText().equals("1.1 수정된 섹션");
    assert updateSectionRes.getData().get("content").asText().equals("섹션 내용입니다.");
    
    // 목차 재조회하여 확인
    TestRes tocAfterRes = userApi.get("/user/books/" + bookId + "/toc");
    tocAfterRes.isSuccess();
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("책 리뷰 기능 테스트")
  void reviewTest() {
    API user1 = apiFactory.user("user1", "user1");
    API user2 = apiFactory.user("user2", "user2");

    // 책 생성
    TestRes createRes = user1.post("/user/books", """
        {
          "title": "리뷰 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // 다른 사용자가 리뷰 작성
    UID user2Id = user2.getSessionUser().getUid();
    Supplier<TestRes> createReview = () -> user2.post("/user/books/" + bookId + "/reviews", """
        {
          "uid": "%s",
          "bookId": "%s",
          "content": "좋은 책입니다!",
          "score": 5
        }
      """.formatted(user2Id.toString(), bookId));

    // 출판된 책에만 리뷰를 작성할 수 있음
    TestRes prePublishReviewRes = createReview.get();
    prePublishReviewRes.is(CommonCode.RESOURCE_PERMISSION_DENIED, "출판되지 않은 책에 접근할 수 없음");
    // 책 출판 (리뷰를 위해 PUBLISHED 상태로 변경)
    user1.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null).isSuccess();
    TestRes reviewRes = createReview.get();
    reviewRes.isSuccess();

    UUID reviewId = UUID.fromString(reviewRes.getData().get("id").asText());

    // 리뷰 수정
    TestRes updateReviewRes = user2.post("/user/books/" + bookId + "/reviews/" + reviewId, """
        {
          "content": "매우 좋은 책입니다!",
          "score": 4
        }
      """);
    updateReviewRes.isSuccess();
    Assertions.assertEquals("매우 좋은 책입니다!", updateReviewRes.getData().get("content").asText());
    Assertions.assertEquals(4, updateReviewRes.getData().get("score").asInt());

    // 리뷰 삭제
    TestRes deleteReviewRes = user2.delete("/user/books/" + bookId + "/reviews/" + reviewId);
    deleteReviewRes.isSuccess();
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("책장 기능 테스트")
  void shelfTest() {
    API userApi = apiFactory.user("user1", "user1");
    API user2Api = apiFactory.user("user2", "user2");

    // 책 생성
    TestRes createRes = userApi.post("/user/books", """
        {
          "title": "책장 테스트 도서"
        }
      """);
    createRes.isSuccess();
    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());

    // 책 출판 (다른 사용자가 볼 수 있도록)
    userApi.post("/user/books/" + bookId + "/status?cmd=PUBLISH", null);

    // 다른 사용자가 책장에 추가
    TestRes addToShelfRes = user2Api.post("/user/books/" + bookId + "/shelf", null);
    addToShelfRes.isSuccess();

    // 책장에서 제거
    TestRes removeFromShelfRes = user2Api.delete("/user/books/" + bookId + "/shelf");
    removeFromShelfRes.isSuccess();
  }
}