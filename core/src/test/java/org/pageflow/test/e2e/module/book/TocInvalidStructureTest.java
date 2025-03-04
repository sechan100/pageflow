package org.pageflow.test.e2e.module.book;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.test.e2e.config.PageflowIntegrationTest;
import org.pageflow.test.e2e.fixture.Users;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.pageflow.test.e2e.shared.TestRes;
import org.pageflow.test.e2e.shared.fixture.Fixture;

import java.util.UUID;

/**
 * 책 목차(TOC) 구조의 무결성을 위반하는 시나리오를 테스트하는 클래스
 * 순환 참조 및 계층 구조를 파괴하는 동작을 검증합니다.
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class TocInvalidStructureTest {
  private final ApiFactory apiFactory;

  @Test
  @Fixture(Users.class)
  @DisplayName("부모 폴더를 자식 폴더로 이동 시 계층 구조 위반 검증")
  void parentToChildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = createTestBook(userApi, "계층 구조 위반 테스트 - 부모->자식 이동");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 폴더 계층 구조 생성: 루트 > 부모 > 자식
    UUID parentFolderId = createFolder(userApi, bookId, rootFolderId, "부모 폴더");
    UUID childFolderId = createFolder(userApi, bookId, parentFolderId, "자식 폴더");

    // 4. 부모 폴더를 자식 폴더로 이동 시도 (계층 구조 위반)
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, parentFolderId, childFolderId));

    // 5. 계층 구조 위반으로 실패 검증
    moveRes.isSuccess();
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("부모 폴더를 손자 폴더로 이동 시 계층 구조 위반 검증")
  void parentToGrandchildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = createTestBook(userApi, "계층 구조 위반 테스트 - 부모->손자 이동");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 3단계 폴더 계층 구조 생성: 루트 > 부모 > 자식 > 손자
    UUID parentFolderId = createFolder(userApi, bookId, rootFolderId, "부모 폴더");
    UUID childFolderId = createFolder(userApi, bookId, parentFolderId, "자식 폴더");
    UUID grandchildFolderId = createFolder(userApi, bookId, childFolderId, "손자 폴더");

    // 4. 부모 폴더를 손자 폴더로 이동 시도 (계층 구조 위반)
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, parentFolderId, grandchildFolderId));

    // 5. 계층 구조 위반으로 실패 검증
    moveRes.isSuccess();
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("루트 폴더 이동 시도 시 실패 검증")
  void rootFolderMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = createTestBook(userApi, "루트 폴더 이동 시도 테스트");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 일반 폴더 생성
    UUID normalFolderId = createFolder(userApi, bookId, rootFolderId, "일반 폴더");

    // 4. 루트 폴더를 일반 폴더로 이동 시도
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, rootFolderId, normalFolderId));

    // 5. 루트 폴더 이동 불가 검증
    moveRes.isSuccess();
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("폴더가 자기 자신을 부모로 지정 시도 시 실패 검증")
  void selfParentTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = createTestBook(userApi, "자기 자신 부모 지정 테스트");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 일반 폴더 생성
    UUID folderAId = createFolder(userApi, bookId, rootFolderId, "폴더 A");

    // 4. 폴더가 자기 자신을 부모로 지정 시도
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderAId, folderAId));

    // 5. 자기 자신을 부모로 지정 불가 검증
    moveRes.isSuccess();
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("복잡한 계층 구조에서 순환 참조 시도 시 실패 검증")
  void complexCyclicReferenceTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = createTestBook(userApi, "복잡한 계층 구조 순환 참조 테스트");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 여러 계층의 폴더 구조 생성
    // 루트 > A > B > C > D
    // 루트 > E > F
    UUID folderAId = createFolder(userApi, bookId, rootFolderId, "폴더 A");
    UUID folderBId = createFolder(userApi, bookId, folderAId, "폴더 B");
    UUID folderCId = createFolder(userApi, bookId, folderBId, "폴더 C");
    UUID folderDId = createFolder(userApi, bookId, folderCId, "폴더 D");

    UUID folderEId = createFolder(userApi, bookId, rootFolderId, "폴더 E");
    UUID folderFId = createFolder(userApi, bookId, folderEId, "폴더 F");

    // 4. 다양한 순환 참조 시도
    // 4.1. A를 D로 이동 시도 (A > B > C > D > A 순환 발생)
    TestRes moveResAtoD = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderAId, folderDId));

    moveResAtoD.isSuccess();
    moveResAtoD.is(BookCode.TOC_HIERARCHY_VIOLATION);

    // 4.2. E를 F로 이동 시도 (E > F > E 순환 발생)
    TestRes moveResEtoF = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderEId, folderFId));

    moveResEtoF.isSuccess();
    moveResEtoF.is(BookCode.TOC_HIERARCHY_VIOLATION);

    // 5. 유효한 이동 확인 (F를 C로 이동 - 순환 참조 없음)
    TestRes validMove = userApi.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderFId, folderCId));

    validMove.isSuccess();

    // 구조 확인: 루트 > A > B > C > F, D
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeParent(tocData, folderFId.toString(), folderCId.toString());
  }

  /**
   * 테스트용 책 생성
   */
  private UUID createTestBook(API api, String title) {
    TestRes createRes = api.post("/user/books", String.format("""
        {
          "title": "%s"
        }
      """, title));
    createRes.isSuccess();
    return UUID.fromString(createRes.getData().get("id").asText());
  }

  /**
   * 폴더 생성
   */
  private UUID createFolder(API api, UUID bookId, UUID parentId, String title) {
    TestRes folderRes = api.post("/user/books/" + bookId + "/toc/folders", String.format("""
        {
          "parentNodeId": "%s",
          "title": "%s"
        }
      """, parentId, title));
    folderRes.isSuccess();
    return UUID.fromString(folderRes.getData().get("id").asText());
  }

  /**
   * 노드 이동
   */
  private TestRes moveNode(API api, UUID bookId, UUID nodeId, UUID destFolderId, int destIndex) {
    return api.post("/user/books/" + bookId + "/toc/replace-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": %d
        }
      """, nodeId, destFolderId, destIndex));
  }

  /**
   * 노드의 부모 검증
   */
  private void assertNodeParent(JsonNode tocData, String nodeId, String expectedParentId) {
    String actualParentId = getParentId(tocData.get("root"), nodeId);
    Assertions.assertEquals(expectedParentId, actualParentId,
      "ID가 " + nodeId + "인 노드의 부모가 예상과 다릅니다");
  }

  /**
   * ID로 노드 검색
   */
  private JsonNode findNodeById(JsonNode node, String id) {
    if(node == null) {
      return null;
    }

    if(node.has("id") && node.get("id").asText().equals(id)) {
      return node;
    }

    if(node.has("children")) {
      JsonNode children = node.get("children");
      for(int i = 0; i < children.size(); i++) {
        JsonNode foundNode = findNodeById(children.get(i), id);
        if(foundNode != null) {
          return foundNode;
        }
      }
    }

    return null;
  }

  /**
   * 특정 노드의 부모 ID 찾기
   */
  private String getParentId(JsonNode node, String childId) {
    if(node == null) {
      return null;
    }

    if(node.has("children")) {
      JsonNode children = node.get("children");
      for(int i = 0; i < children.size(); i++) {
        String currentChildId = children.get(i).get("id").asText();
        if(currentChildId.equals(childId)) {
          return node.get("id").asText();
        }

        String foundParentId = getParentId(children.get(i), childId);
        if(foundParentId != null) {
          return foundParentId;
        }
      }
    }

    return null;
  }
}