package org.pageflow.test.module.book.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.TestRes;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class TocUtil {
  /**
   * 테스트용 책 생성
   */
  public static UUID createTestBook(API api, String title) {
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
  public static UUID createFolder(API api, UUID bookId, UUID parentId, String title) {
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
  public static UUID createSection(API api, UUID bookId, UUID parentId, String title) {
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
  public static TestRes moveNode(API api, UUID bookId, UUID nodeId, UUID destFolderId, int destIndex) {
    TestRes moveRes = api.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": %d
        }
      """, nodeId, destFolderId, destIndex));
    moveRes.isSuccess();
    return moveRes;
  }

  /**
   * 섹션 업데이트
   */
  public static void updateSection(API api, UUID bookId, UUID sectionId, String title, String content) {
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
  public static void updateFolder(API api, UUID bookId, UUID folderId, String title) {
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
  public static void deleteFolder(API api, UUID bookId, UUID folderId) {
    TestRes deleteRes = api.delete("/user/books/" + bookId + "/toc/folders/" + folderId);
    deleteRes.isSuccess();
  }

  /**
   * 섹션 삭제
   */
  public static void deleteSection(API api, UUID bookId, UUID sectionId) {
    TestRes deleteRes = api.delete("/user/books/" + bookId + "/toc/sections/" + sectionId);
    deleteRes.isSuccess();
  }

  /**
   * 목차 구조의 최대 깊이 검증
   */
  public static void assertTocStructure(JsonNode tocData, int expectedDepth) {
    int actualDepth = calculateMaxDepth(tocData.get("root"));
    Assertions.assertEquals(expectedDepth, actualDepth, "목차 구조의 최대 깊이가 예상과 다릅니다");
  }

  /**
   * 노드의 자식 수 검증
   */
  public static void assertNodeChildrenCount(JsonNode tocData, String nodeId, int expectedCount) {
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
  public static void assertChildCount(JsonNode rootNode, int expectedCount) {
    Assertions.assertEquals(expectedCount, rootNode.get("children").size(),
      "루트 노드의 자식 수가 예상과 다릅니다");
  }

  /**
   * 노드의 부모 검증
   */
  public static void assertNodeParent(JsonNode tocData, String nodeId, String expectedParentId) {
    JsonNode node = findNodeById(tocData.get("root"), nodeId);
    Assertions.assertNotNull(node, "ID가 " + nodeId + "인 노드를 찾을 수 없습니다");

    String actualParentId = getParentId(tocData.get("root"), nodeId);
    Assertions.assertEquals(expectedParentId, actualParentId,
      "ID가 " + nodeId + "인 노드의 부모가 예상과 다릅니다");
  }

  /**
   * 노드의 존재 여부 검증
   */
  public static void assertNodeExists(JsonNode tocData, String nodeId, boolean shouldExist) {
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
  public static void assertNodeOrder(JsonNode tocData, String parentId, List<UUID> expectedOrder) {
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
  private static int calculateMaxDepth(JsonNode node) {
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
  private static JsonNode findNodeById(JsonNode node, String id) {
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
  private static String getParentId(JsonNode node, String childId) {
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
