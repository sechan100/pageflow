package org.pageflow.test.e2e.module.book;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.test.e2e.config.PageflowIntegrationTest;
import org.pageflow.test.e2e.fixture.Users;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.ApiFactory;
import org.pageflow.test.e2e.shared.TestRes;
import org.pageflow.test.e2e.shared.fixture.Fixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 책 목차(TOC) 구조의 생성, 수정, 삭제, 이동 기능을 철저하게 테스트하는 클래스
 * 다양한 계층 구조와 예외 케이스를 검증합니다.
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class TocStructureTest {
  private final ApiFactory apiFactory;

  @Test
  @Fixture(Users.class)
  @SuppressWarnings("OverlyLongMethod")
  @DisplayName("목차 계층 구조 생성 및 이동 복합 테스트")
  void tocComplexStructureTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = createTestBook(userApi, "목차 구조 테스트 도서");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());
    assertTocStructure(tocData, 1); // 루트만 있으므로 깊이 1

    // 3. 계층 구조 생성: 1단계
    // 루트 밑에 3개의 폴더 생성
    UUID folder1Id = createFolder(userApi, bookId, rootFolderId, "1장. 서론");
    UUID folder2Id = createFolder(userApi, bookId, rootFolderId, "2장. 본론");
    UUID folder3Id = createFolder(userApi, bookId, rootFolderId, "3장. 결론");

    // 현재 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertTocStructure(tocData, 2); // 루트 아래 3개 폴더, 깊이 2
    assertChildCount(tocData.get("root"), 3);

    // 4. 섹션 추가
    // 1장에 섹션 1개 추가
    UUID section1Id = createSection(userApi, bookId, folder1Id, "1.1 서론 개요");

    // 2장에 섹션 3개 추가
    UUID section2Id = createSection(userApi, bookId, folder2Id, "2.1 주제 설명");
    UUID section3Id = createSection(userApi, bookId, folder2Id, "2.2 논점 분석");
    UUID section4Id = createSection(userApi, bookId, folder2Id, "2.3 사례 연구");

    // 3장에 섹션 1개 추가
    UUID section5Id = createSection(userApi, bookId, folder3Id, "3.1 결론 요약");

    // 현재 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertTocStructure(tocData, 3); // 루트(1) > 폴더(2) > 섹션(3) 구조이므로 깊이 3
    assertNodeChildrenCount(tocData, folder1Id.toString(), 1);
    assertNodeChildrenCount(tocData, folder2Id.toString(), 3);
    assertNodeChildrenCount(tocData, folder3Id.toString(), 1);

    // 5. 중첩 폴더 생성: 2단계
    // 2장 아래에 하위 폴더 생성
    UUID subFolder1Id = createFolder(userApi, bookId, folder2Id, "2.A 상세분석");

    // 하위 폴더에 섹션 추가
    UUID section6Id = createSection(userApi, bookId, subFolder1Id, "2.A.1 세부 내용");
    UUID section7Id = createSection(userApi, bookId, subFolder1Id, "2.A.2 추가 분석");

    // 현재 구조 확인 - 루트 > 2장 > 2.A 상세분석 > 섹션
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertTocStructure(tocData, 4); // 루트(1) > 2장(2) > 2.A(3) > 섹션(4) 이므로 깊이 4
    assertNodeChildrenCount(tocData, subFolder1Id.toString(), 2);

    // 6. 3단계 중첩 폴더 및 섹션 생성
    UUID subSubFolderId = createFolder(userApi, bookId, subFolder1Id, "2.A.X 특별 사례");
    UUID section8Id = createSection(userApi, bookId, subSubFolderId, "2.A.X.1 특별 케이스");

    // 현재 구조 확인 - 루트 > 2장 > 2.A > 2.A.X > 섹션
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertTocStructure(tocData, 5); // 루트(1) > 2장(2) > 2.A(3) > 2.A.X(4) > 섹션(5) 이므로 깊이 5
    assertNodeChildrenCount(tocData, subSubFolderId.toString(), 1);

    // 7. 노드 이동 테스트 - 같은 레벨에서 순서 변경
    // 2장 내의 섹션들 순서 변경: 2.2, 2.3, 2.1 순서로 변경
    moveNode(userApi, bookId, section2Id, folder2Id, 2); // 2.1을 마지막으로

    // 순서 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeOrder(tocData, folder2Id.toString(), Arrays.asList(section3Id, section4Id, section2Id, subFolder1Id)); // subFolder1Id도 여기에 있어야 함
    assertTocStructure(tocData, 5); // 구조 깊이는 여전히 5

    // 8. 노드 이동 테스트 - 다른 부모로 이동
    // 2.A.X.1 섹션을 1장으로 이동
    moveNode(userApi, bookId, section8Id, folder1Id, 1);

    // 이동 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeParent(tocData, section8Id.toString(), folder1Id.toString());
    assertNodeChildrenCount(tocData, folder1Id.toString(), 2);
    assertNodeChildrenCount(tocData, subSubFolderId.toString(), 0);
    assertTocStructure(tocData, 4); // 최대 깊이가 4로 감소 (2.A.X 폴더 하위에 2.A.X.1가 1장으로 이동하여 유일한 최하위 노드가 없어짐.)

    // 9. 계층 이동 - 폴더를 상위로 이동
    // 2.A 폴더를 루트로 이동
    moveNode(userApi, bookId, subFolder1Id, rootFolderId, 1);

    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeParent(tocData, subFolder1Id.toString(), rootFolderId.toString());
    assertNodeChildrenCount(tocData, folder2Id.toString(), 3); // 섹션 3개만 남음
    assertTocStructure(tocData, 3); // 이제 최대 깊이는 3; 루트(1) > 2.A(2) > 2.A.X(3, 비어있음)

    // 10. 계층 이동 - 폴더를 하위 레벨로 이동
    // 3장을 2.A 폴더 안으로 이동
    moveNode(userApi, bookId, folder3Id, subFolder1Id, 0);

    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeParent(tocData, folder3Id.toString(), subFolder1Id.toString());
    assertChildCount(tocData.get("root"), 3); // 루트 아래 1장, 2장, 2.A
    assertTocStructure(tocData, 4); // 최대 깊이는 4; root > 2.A > 3장 > 3.1

    // 11. 섹션 업데이트 테스트
    String updatedTitle = "업데이트된, 섹션 제목";
    String updatedContent = "이것은 업데이트된 내용입니다.";
    updateSection(userApi, bookId, section1Id, updatedTitle, updatedContent);

    TestRes sectionRes = userApi.get("/user/books/" + bookId + "/toc/sections/" + section1Id + "/content");
    sectionRes.isSuccess();
    Assertions.assertEquals(updatedTitle, sectionRes.getData().get("title").asText());
    Assertions.assertEquals(updatedContent, sectionRes.getData().get("content").asText());

    // 12. 폴더 업데이트 테스트
    String updatedFolderTitle = "2장. 수정된 본론";
    updateFolder(userApi, bookId, folder2Id, updatedFolderTitle);

    TestRes folderRes = userApi.get("/user/books/" + bookId + "/toc/folders/" + folder2Id);
    folderRes.isSuccess();
    Assertions.assertEquals(updatedFolderTitle, folderRes.getData().get("title").asText());

    // 13. 폴더 삭제 테스트 (하위 구조를 포함한 폴더, 2.A)
    deleteFolder(userApi, bookId, subFolder1Id);

    // 삭제 후 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeExists(tocData, subFolder1Id.toString(), false); // 삭제된 폴더
    assertNodeExists(tocData, folder3Id.toString(), false); // 삭제된 폴더의 하위 폴더도 삭제됨
    assertNodeExists(tocData, subSubFolderId.toString(), false); // 2.A.X 폴더도 삭제됨
    assertTocStructure(tocData, 3); // 이제 최대 깊이는 3; root > 1, 2장 > 섹션들

    // 14. 섹션 삭제 테스트(2.1)
    deleteSection(userApi, bookId, section2Id);

    // 섹션 삭제 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeExists(tocData, section2Id.toString(), false);
    assertNodeChildrenCount(tocData, folder2Id.toString(), 2); // 남은 섹션 2개

    // 15. 극단적인 계층 이동 - 모두 root로 이동시켜서 계층 구조 평탄화
    List<UUID> remainingSections = new ArrayList<>();
    remainingSections.add(section1Id);
    remainingSections.add(section8Id);
    remainingSections.add(section3Id);
    remainingSections.add(section4Id);

    for(int i = 0; i < remainingSections.size(); i++) {
      moveNode(userApi, bookId, remainingSections.get(i), rootFolderId, i + 2);
    }

    // 모든 노드가 루트 아래로 이동되었는지 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    for(UUID sectionId : remainingSections) {
      assertNodeParent(tocData, sectionId.toString(), rootFolderId.toString());
    }
    assertTocStructure(tocData, 2); // 모두 루트 직접 아래로 이동했으므로 최대 깊이 2 (루트(1) > 섹션/폴더(2))

    // 16. 남은 모든 폴더 삭제 확인
    deleteFolder(userApi, bookId, folder1Id);
    deleteFolder(userApi, bookId, folder2Id);

    // 최종 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    assertNodeExists(tocData, folder1Id.toString(), false);
    assertNodeExists(tocData, folder2Id.toString(), false);
    assertTocStructure(tocData, 2); // 루트(1) > 섹션(2)이므로 깊이 2

    JsonNode rootChildren = tocData.get("root").get("children");
    // 남은 섹션들만 있는지 확인
    for(int i = 0; i < rootChildren.size(); i++) {
      String nodeType = rootChildren.get(i).get("type").asText();
      if(nodeType.equals("SECTION")) {
        String nodeId = rootChildren.get(i).get("id").asText();
        boolean found = false;
        for(UUID sectionId : remainingSections) {
          if(sectionId.toString().equals(nodeId)) {
            found = true;
            break;
          }
        }
        Assertions.assertTrue(found, "예상치 못한 섹션이 존재합니다: " + nodeId);
      }
    }
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
   * 섹션 생성
   */
  private UUID createSection(API api, UUID bookId, UUID parentId, String title) {
    TestRes sectionRes = api.post("/user/books/" + bookId + "/toc/sections", String.format("""
        {
          "parentNodeId": "%s",
          "title": "%s"
        }
      """, parentId, title));
    sectionRes.isSuccess();
    return UUID.fromString(sectionRes.getData().get("id").asText());
  }

  /**
   * 노드 이동
   */
  private void moveNode(API api, UUID bookId, UUID nodeId, UUID destFolderId, int destIndex) {
    TestRes moveRes = api.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": %d
        }
      """, nodeId, destFolderId, destIndex));
    moveRes.isSuccess();
  }

  /**
   * 섹션 업데이트
   */
  private void updateSection(API api, UUID bookId, UUID sectionId, String title, String content) {
    TestRes updateRes = api.post("/user/books/" + bookId + "/toc/sections/" + sectionId, String.format("""
        {
          "title": "%s",
          "content": "%s"
        }
      """, title, content));
    updateRes.isSuccess();
  }

  /**
   * 폴더 업데이트
   */
  private void updateFolder(API api, UUID bookId, UUID folderId, String title) {
    TestRes updateRes = api.post("/user/books/" + bookId + "/toc/folders/" + folderId, String.format("""
        {
          "title": "%s"
        }
      """, title));
    updateRes.isSuccess();
  }

  /**
   * 폴더 삭제
   */
  private void deleteFolder(API api, UUID bookId, UUID folderId) {
    TestRes deleteRes = api.delete("/user/books/" + bookId + "/toc/folders/" + folderId);
    deleteRes.isSuccess();
  }

  /**
   * 섹션 삭제
   */
  private void deleteSection(API api, UUID bookId, UUID sectionId) {
    TestRes deleteRes = api.delete("/user/books/" + bookId + "/toc/sections/" + sectionId);
    deleteRes.isSuccess();
  }

  // === 검증 헬퍼 메소드 ===

  /**
   * 목차 구조의 최대 깊이 검증
   */
  private void assertTocStructure(JsonNode tocData, int expectedDepth) {
    int actualDepth = calculateMaxDepth(tocData.get("root"));
    Assertions.assertEquals(expectedDepth, actualDepth, "목차 구조의 최대 깊이가 예상과 다릅니다");
  }

  /**
   * 노드의 자식 수 검증
   */
  private void assertNodeChildrenCount(JsonNode tocData, String nodeId, int expectedCount) {
    JsonNode node = findNodeById(tocData.get("root"), nodeId);
    Assertions.assertNotNull(node, "ID가 " + nodeId + "인 노드를 찾을 수 없습니다");

    if(node.has("children")) {
      Assertions.assertEquals(expectedCount, node.get("children").size(),
        "ID가 " + nodeId + "인 노드의 자식 수가 예상과 다릅니다");
    } else {
      Assertions.assertEquals(0, expectedCount,
        "ID가 " + nodeId + "인 노드에 자식이 없어야 합니다");
    }
  }

  /**
   * 루트 노드의 직접 자식 수 검증
   */
  private void assertChildCount(JsonNode rootNode, int expectedCount) {
    Assertions.assertEquals(expectedCount, rootNode.get("children").size(),
      "루트 노드의 자식 수가 예상과 다릅니다");
  }

  /**
   * 노드의 부모 검증
   */
  private void assertNodeParent(JsonNode tocData, String nodeId, String expectedParentId) {
    JsonNode node = findNodeById(tocData.get("root"), nodeId);
    Assertions.assertNotNull(node, "ID가 " + nodeId + "인 노드를 찾을 수 없습니다");

    String actualParentId = getParentId(tocData.get("root"), nodeId);
    Assertions.assertEquals(expectedParentId, actualParentId,
      "ID가 " + nodeId + "인 노드의 부모가 예상과 다릅니다");
  }

  /**
   * 노드의 존재 여부 검증
   */
  private void assertNodeExists(JsonNode tocData, String nodeId, boolean shouldExist) {
    JsonNode node = findNodeById(tocData.get("root"), nodeId);
    if(shouldExist) {
      Assertions.assertNotNull(node, "ID가 " + nodeId + "인 노드가 존재해야 합니다");
    } else {
      Assertions.assertNull(node, "ID가 " + nodeId + "인 노드가 존재하지 않아야 합니다");
    }
  }

  /**
   * 노드의 순서 검증
   */
  private void assertNodeOrder(JsonNode tocData, String parentId, List<UUID> expectedOrder) {
    JsonNode parentNode = findNodeById(tocData.get("root"), parentId);
    Assertions.assertNotNull(parentNode, "ID가 " + parentId + "인 부모 노드를 찾을 수 없습니다");

    if(!parentNode.has("children")) {
      Assertions.fail("ID가 " + parentId + "인 부모 노드에 자식이 없습니다");
    }

    JsonNode children = parentNode.get("children");
    Assertions.assertEquals(expectedOrder.size(), children.size(),
      "ID가 " + parentId + "인 부모 노드의 자식 수가 예상과 다릅니다");

    for(int i = 0; i < expectedOrder.size(); i++) {
      String expectedId = expectedOrder.get(i).toString();
      String actualId = children.get(i).get("id").asText();
      Assertions.assertEquals(expectedId, actualId,
        "인덱스 " + i + "의 자식 노드 ID가 예상과 다릅니다");
    }
  }

  /**
   * 노드의 최대 깊이 계산
   */
  private int calculateMaxDepth(JsonNode node) {
    if(node == null) {
      return 0;
    }

    int maxDepth = 1; // 현재 노드 자체를 포함

    if(node.has("children") && node.get("children").size() > 0) {
      JsonNode children = node.get("children");
      int childMaxDepth = 0;

      for(int i = 0; i < children.size(); i++) {
        int depth = calculateMaxDepth(children.get(i));
        if(depth > childMaxDepth) {
          childMaxDepth = depth;
        }
      }

      maxDepth += childMaxDepth;
    }

    return maxDepth;
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