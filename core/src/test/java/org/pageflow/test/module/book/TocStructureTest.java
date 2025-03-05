package org.pageflow.test.module.book;

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
import org.pageflow.test.module.book.utils.TocUtil;

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
    UUID bookId = TocUtil.createTestBook(userApi, "목차 구조 테스트 도서");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());
    TocUtil.assertTocStructure(tocData, 1); // 루트만 있으므로 깊이 1

    // 3. 계층 구조 생성: 1단계
    // 루트 밑에 3개의 폴더 생성
    UUID folder1Id = TocUtil.createFolder(userApi, bookId, rootFolderId, "1장. 서론");
    UUID folder2Id = TocUtil.createFolder(userApi, bookId, rootFolderId, "2장. 본론");
    UUID folder3Id = TocUtil.createFolder(userApi, bookId, rootFolderId, "3장. 결론");

    // 현재 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertTocStructure(tocData, 2); // 루트 아래 3개 폴더, 깊이 2
    TocUtil.assertChildCount(tocData.get("root"), 3);

    // 4. 섹션 추가
    // 1장에 섹션 1개 추가
    UUID section1Id = TocUtil.createSection(userApi, bookId, folder1Id, "1.1 서론 개요");

    // 2장에 섹션 3개 추가
    UUID section2Id = TocUtil.createSection(userApi, bookId, folder2Id, "2.1 주제 설명");
    UUID section3Id = TocUtil.createSection(userApi, bookId, folder2Id, "2.2 논점 분석");
    UUID section4Id = TocUtil.createSection(userApi, bookId, folder2Id, "2.3 사례 연구");

    // 3장에 섹션 1개 추가
    UUID section5Id = TocUtil.createSection(userApi, bookId, folder3Id, "3.1 결론 요약");

    // 현재 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertTocStructure(tocData, 3); // 루트(1) > 폴더(2) > 섹션(3) 구조이므로 깊이 3
    TocUtil.assertNodeChildrenCount(tocData, folder1Id.toString(), 1);
    TocUtil.assertNodeChildrenCount(tocData, folder2Id.toString(), 3);
    TocUtil.assertNodeChildrenCount(tocData, folder3Id.toString(), 1);

    // 5. 중첩 폴더 생성: 2단계
    // 2장 아래에 하위 폴더 생성
    UUID subFolder1Id = TocUtil.createFolder(userApi, bookId, folder2Id, "2.A 상세분석");

    // 하위 폴더에 섹션 추가
    UUID section6Id = TocUtil.createSection(userApi, bookId, subFolder1Id, "2.A.1 세부 내용");
    UUID section7Id = TocUtil.createSection(userApi, bookId, subFolder1Id, "2.A.2 추가 분석");

    // 현재 구조 확인 - 루트 > 2장 > 2.A 상세분석 > 섹션
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertTocStructure(tocData, 4); // 루트(1) > 2장(2) > 2.A(3) > 섹션(4) 이므로 깊이 4
    TocUtil.assertNodeChildrenCount(tocData, subFolder1Id.toString(), 2);

    // 6. 3단계 중첩 폴더 및 섹션 생성
    UUID subSubFolderId = TocUtil.createFolder(userApi, bookId, subFolder1Id, "2.A.X 특별 사례");
    UUID section8Id = TocUtil.createSection(userApi, bookId, subSubFolderId, "2.A.X.1 특별 케이스");

    // 현재 구조 확인 - 루트 > 2장 > 2.A > 2.A.X > 섹션
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertTocStructure(tocData, 5); // 루트(1) > 2장(2) > 2.A(3) > 2.A.X(4) > 섹션(5) 이므로 깊이 5
    TocUtil.assertNodeChildrenCount(tocData, subSubFolderId.toString(), 1);

    // 7. 노드 이동 테스트 - 같은 레벨에서 순서 변경
    // 2장 내의 섹션들 순서 변경: 2.2, 2.3, 2.1 순서로 변경
    TocUtil.moveNode(userApi, bookId, section2Id, folder2Id, 2); // 2.1을 마지막으로

    // 순서 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeOrder(tocData, folder2Id.toString(), Arrays.asList(section3Id, section4Id, section2Id, subFolder1Id)); // subFolder1Id도 여기에 있어야 함
    TocUtil.assertTocStructure(tocData, 5); // 구조 깊이는 여전히 5

    // 8. 노드 이동 테스트 - 다른 부모로 이동
    // 2.A.X.1 섹션을 1장으로 이동
    TocUtil.moveNode(userApi, bookId, section8Id, folder1Id, 1);

    // 이동 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeParent(tocData, section8Id.toString(), folder1Id.toString());
    TocUtil.assertNodeChildrenCount(tocData, folder1Id.toString(), 2);
    TocUtil.assertNodeChildrenCount(tocData, subSubFolderId.toString(), 0);
    TocUtil.assertTocStructure(tocData, 4); // 최대 깊이가 4로 감소 (2.A.X 폴더 하위에 2.A.X.1가 1장으로 이동하여 유일한 최하위 노드가 없어짐.)

    // 9. 계층 이동 - 폴더를 상위로 이동
    // 2.A 폴더를 루트로 이동
    TocUtil.moveNode(userApi, bookId, subFolder1Id, rootFolderId, 1);

    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeParent(tocData, subFolder1Id.toString(), rootFolderId.toString());
    TocUtil.assertNodeChildrenCount(tocData, folder2Id.toString(), 3); // 섹션 3개만 남음
    TocUtil.assertTocStructure(tocData, 3); // 이제 최대 깊이는 3; 루트(1) > 2.A(2) > 2.A.X(3, 비어있음)

    // 10. 계층 이동 - 폴더를 하위 레벨로 이동
    // 3장을 2.A 폴더 안으로 이동
    TocUtil.moveNode(userApi, bookId, folder3Id, subFolder1Id, 0);

    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeParent(tocData, folder3Id.toString(), subFolder1Id.toString());
    TocUtil.assertChildCount(tocData.get("root"), 3); // 루트 아래 1장, 2장, 2.A
    TocUtil.assertTocStructure(tocData, 4); // 최대 깊이는 4; root > 2.A > 3장 > 3.1

    // 11. 섹션 업데이트 테스트
    String updatedTitle = "업데이트된, 섹션 제목";
    String updatedContent = "이것은 업데이트된 내용입니다.";
    TocUtil.updateSection(userApi, bookId, section1Id, updatedTitle, updatedContent);

    TestRes sectionRes = userApi.get("/user/books/" + bookId + "/toc/sections/" + section1Id + "/content");
    sectionRes.isSuccess();
    Assertions.assertEquals(updatedTitle, sectionRes.getData().get("title").asText());
    Assertions.assertEquals(updatedContent, sectionRes.getData().get("content").asText());

    // 12. 폴더 업데이트 테스트
    String updatedFolderTitle = "2장. 수정된 본론";
    TocUtil.updateFolder(userApi, bookId, folder2Id, updatedFolderTitle);

    TestRes folderRes = userApi.get("/user/books/" + bookId + "/toc/folders/" + folder2Id);
    folderRes.isSuccess();
    Assertions.assertEquals(updatedFolderTitle, folderRes.getData().get("title").asText());

    // 13. 폴더 삭제 테스트 (하위 구조를 포함한 폴더, 2.A)
    TocUtil.deleteFolder(userApi, bookId, subFolder1Id);

    // 삭제 후 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeExists(tocData, subFolder1Id.toString(), false); // 삭제된 폴더
    TocUtil.assertNodeExists(tocData, folder3Id.toString(), false); // 삭제된 폴더의 하위 폴더도 삭제됨
    TocUtil.assertNodeExists(tocData, subSubFolderId.toString(), false); // 2.A.X 폴더도 삭제됨
    TocUtil.assertTocStructure(tocData, 3); // 이제 최대 깊이는 3; root > 1, 2장 > 섹션들

    // 14. 섹션 삭제 테스트(2.1)
    TocUtil.deleteSection(userApi, bookId, section2Id);

    // 섹션 삭제 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeExists(tocData, section2Id.toString(), false);
    TocUtil.assertNodeChildrenCount(tocData, folder2Id.toString(), 2); // 남은 섹션 2개

    // 15. 극단적인 계층 이동 - 모두 root로 이동시켜서 계층 구조 평탄화
    List<UUID> remainingSections = new ArrayList<>();
    remainingSections.add(section1Id);
    remainingSections.add(section8Id);
    remainingSections.add(section3Id);
    remainingSections.add(section4Id);

    for(int i = 0; i < remainingSections.size(); i++) {
      TocUtil.moveNode(userApi, bookId, remainingSections.get(i), rootFolderId, i + 2);
    }

    // 모든 노드가 루트 아래로 이동되었는지 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    for(UUID sectionId : remainingSections) {
      TocUtil.assertNodeParent(tocData, sectionId.toString(), rootFolderId.toString());
    }
    TocUtil.assertTocStructure(tocData, 2); // 모두 루트 직접 아래로 이동했으므로 최대 깊이 2 (루트(1) > 섹션/폴더(2))

    // 16. 남은 모든 폴더 삭제 확인
    TocUtil.deleteFolder(userApi, bookId, folder1Id);
    TocUtil.deleteFolder(userApi, bookId, folder2Id);

    // 최종 구조 확인
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeExists(tocData, folder1Id.toString(), false);
    TocUtil.assertNodeExists(tocData, folder2Id.toString(), false);
    TocUtil.assertTocStructure(tocData, 2); // 루트(1) > 섹션(2)이므로 깊이 2

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

  @Test
  @Fixture(Users.class)
  @DisplayName("부모 폴더를 자식 폴더로 이동 시 계층 구조 위반 검증")
  void parentToChildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = TocUtil.createTestBook(userApi, "계층 구조 위반 테스트 - 부모->자식 이동");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 폴더 계층 구조 생성: 루트 > 부모 > 자식
    UUID parentFolderId = TocUtil.createFolder(userApi, bookId, rootFolderId, "부모 폴더");
    UUID childFolderId = TocUtil.createFolder(userApi, bookId, parentFolderId, "자식 폴더");

    // 4. 부모 폴더를 자식 폴더로 이동 시도 (계층 구조 위반)
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, parentFolderId, childFolderId));

    // 5. 계층 구조 위반으로 실패 검증
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("부모 폴더를 손자 폴더로 이동 시 계층 구조 위반 검증")
  void parentToGrandchildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = TocUtil.createTestBook(userApi, "계층 구조 위반 테스트 - 부모->손자 이동");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 3단계 폴더 계층 구조 생성: 루트 > 부모 > 자식 > 손자
    UUID parentFolderId = TocUtil.createFolder(userApi, bookId, rootFolderId, "부모 폴더");
    UUID childFolderId = TocUtil.createFolder(userApi, bookId, parentFolderId, "자식 폴더");
    UUID grandchildFolderId = TocUtil.createFolder(userApi, bookId, childFolderId, "손자 폴더");

    // 4. 부모 폴더를 손자 폴더로 이동 시도 (계층 구조 위반)
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, parentFolderId, grandchildFolderId));

    // 5. 계층 구조 위반으로 실패 검증
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("루트 폴더 이동 시도 시 실패 검증")
  void rootFolderMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = TocUtil.createTestBook(userApi, "루트 폴더 이동 시도 테스트");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 일반 폴더 생성
    UUID normalFolderId = TocUtil.createFolder(userApi, bookId, rootFolderId, "일반 폴더");

    // 4. 루트 폴더를 일반 폴더로 이동 시도
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, rootFolderId, normalFolderId));

    // 5. 루트 폴더 이동 불가 검증
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("폴더가 자기 자신을 부모로 지정 시도 시 실패 검증")
  void selfParentTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = TocUtil.createTestBook(userApi, "자기 자신 부모 지정 테스트");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 일반 폴더 생성
    UUID folderAId = TocUtil.createFolder(userApi, bookId, rootFolderId, "폴더 A");

    // 4. 폴더가 자기 자신을 부모로 지정 시도
    TestRes moveRes = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderAId, folderAId));

    // 5. 자기 자신을 부모로 지정 불가 검증
    moveRes.is(BookCode.TOC_HIERARCHY_VIOLATION);
  }

  @Test
  @Fixture(Users.class)
  @DisplayName("복잡한 계층 구조에서 순환 참조 시도 시 실패 검증")
  void complexCyclicReferenceTest() {
    // 1. 테스트 셋업 및 책 생성
    API userApi = apiFactory.user("user1", "user1");
    UUID bookId = TocUtil.createTestBook(userApi, "복잡한 계층 구조 순환 참조 테스트");

    // 2. 기본 루트 폴더 확인
    JsonNode tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    UUID rootFolderId = UUID.fromString(tocData.get("root").get("id").asText());

    // 3. 여러 계층의 폴더 구조 생성
    // 루트 > A > B > C > D
    // 루트 > E > F
    UUID folderAId = TocUtil.createFolder(userApi, bookId, rootFolderId, "폴더 A");
    UUID folderBId = TocUtil.createFolder(userApi, bookId, folderAId, "폴더 B");
    UUID folderCId = TocUtil.createFolder(userApi, bookId, folderBId, "폴더 C");
    UUID folderDId = TocUtil.createFolder(userApi, bookId, folderCId, "폴더 D");

    UUID folderEId = TocUtil.createFolder(userApi, bookId, rootFolderId, "폴더 E");
    UUID folderFId = TocUtil.createFolder(userApi, bookId, folderEId, "폴더 F");

    // 4. 다양한 순환 참조 시도
    // 4.1. A를 D로 이동 시도 (A > B > C > D > A 순환 발생)
    TestRes moveResAtoD = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderAId, folderDId));

    moveResAtoD.is(BookCode.TOC_HIERARCHY_VIOLATION);

    // 4.2. E를 F로 이동 시도 (E > F > E 순환 발생)
    TestRes moveResEtoF = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderEId, folderFId));

    moveResEtoF.is(BookCode.TOC_HIERARCHY_VIOLATION);

    // 5. 유효한 이동 확인 (F를 C로 이동 - 순환 참조 없음)
    TestRes validMove = userApi.post("/user/books/" + bookId + "/toc/relocate-node", String.format("""
        {
          "targetNodeId": "%s",
          "destFolderId": "%s",
          "destIndex": 0
        }
      """, folderFId, folderCId));

    validMove.isSuccess();

    // 구조 확인: 루트 > A > B > C > F, D
    tocData = userApi.get("/user/books/" + bookId + "/toc").getData();
    TocUtil.assertNodeParent(tocData, folderFId.toString(), folderCId.toString());
  }
}