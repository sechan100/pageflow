package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.adapter.out.TocNodeJpaRepository;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.TocNodeType;
import org.pageflow.book.application.dto.*;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.NodeIdentifier;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 책 목차(TOC) 구조의 생성, 수정, 삭제, 이동 기능을 철저하게 테스트하는 클래스
 * 다양한 계층 구조와 예외 케이스를 검증합니다.
 */
@PageflowTest
@RequiredArgsConstructor
public class TocStructureTest {
  private final UserUtils userUtils;
  private final BookUtils bookUtils;
  private final EditTocUseCase editTocUseCase;
  private final SectionWriteUseCase sectionWriteUseCase;
  private final TocNodeJpaRepository nodePersistencePort;

  @Test
  @SuppressWarnings("OverlyLongMethod")
  @DisplayName("목차 계층 구조 생성 및 이동 복합 테스트")
  void tocComplexStructureTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user1 = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user1, "목차 구조 테스트 도서");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto.Toc toc = editTocUseCase.getToc(bookId);
    UUID rootFolderId = toc.getRoot().getId();
    assertTocStructure(toc, 1); // 루트만 있으므로 깊이 1

    // 3. 계층 구조 생성: 1단계
    // 루트 밑에 3개의 폴더 생성
    Result<FolderDto> folder1Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user1.getUid(),
        bookId,
        rootFolderId,
        "1장. 서론"
      )
    );
    Assertions.assertTrue(folder1Result.isSuccess());
    UUID folder1Id = folder1Result.getSuccessData().getId();

    Result<FolderDto> folder2Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user1.getUid(),
        bookId,
        rootFolderId,
        "2장. 본론"
      )
    );
    Assertions.assertTrue(folder2Result.isSuccess());
    UUID folder2Id = folder2Result.getSuccessData().getId();

    Result<FolderDto> folder3Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user1.getUid(),
        bookId,
        rootFolderId,
        "3장. 결론"
      )
    );
    Assertions.assertTrue(folder3Result.isSuccess());
    UUID folder3Id = folder3Result.getSuccessData().getId();

    // 현재 구조 확인
    toc = editTocUseCase.getToc(bookId);
    assertTocStructure(toc, 2); // 루트 아래 3개 폴더, 깊이 2
    assertChildCount(toc.getRoot(), 3);

    // 4. 섹션 추가
    // 1장에 섹션 1개 추가
    Result<SectionDtoWithContent> section1Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        folder1Id,
        "1.1 서론 개요"
      )
    );
    Assertions.assertTrue(section1Result.isSuccess());
    UUID section1Id = section1Result.getSuccessData().getId();

    // 2장에 섹션 3개 추가
    Result<SectionDtoWithContent> section2Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        folder2Id,
        "2.1 주제 설명"
      )
    );
    Assertions.assertTrue(section2Result.isSuccess());
    UUID section2Id = section2Result.getSuccessData().getId();

    Result<SectionDtoWithContent> section3Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        folder2Id,
        "2.2 논점 분석"
      )
    );
    Assertions.assertTrue(section3Result.isSuccess());
    UUID section3Id = section3Result.getSuccessData().getId();

    Result<SectionDtoWithContent> section4Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        folder2Id,
        "2.3 사례 연구"
      )
    );
    Assertions.assertTrue(section4Result.isSuccess());
    UUID section4Id = section4Result.getSuccessData().getId();

    // 3장에 섹션 1개 추가
    Result<SectionDtoWithContent> section5Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        folder3Id,
        "3.1 결론 요약"
      )
    );
    Assertions.assertTrue(section5Result.isSuccess());
    UUID section5Id = section5Result.getSuccessData().getId();

    // 현재 구조 확인
    toc = editTocUseCase.getToc(bookId);
    assertTocStructure(toc, 3); // 루트(1) > 폴더(2) > 섹션(3) 구조이므로 깊이 3
    assertNodeChildrenCount(toc, folder1Id, 1);
    assertNodeChildrenCount(toc, folder2Id, 3);
    assertNodeChildrenCount(toc, folder3Id, 1);

    // 5. 중첩 폴더 생성: 2단계
    // 2장 아래에 하위 폴더 생성
    Result<FolderDto> subFolder1Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user1.getUid(),
        bookId,
        folder2Id,
        "2.A 상세 분석"
      )
    );
    Assertions.assertTrue(subFolder1Result.isSuccess());
    UUID subFolder1Id = subFolder1Result.getSuccessData().getId();

    // 하위 폴더에 섹션 추가
    Result<SectionDtoWithContent> section6Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        subFolder1Id,
        "2.A.1 세부 내용"
      )
    );
    Assertions.assertTrue(section6Result.isSuccess());
    UUID section6Id = section6Result.getSuccessData().getId();

    Result<SectionDtoWithContent> section7Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        subFolder1Id,
        "2.A.2 추가 분석"
      )
    );
    Assertions.assertTrue(section7Result.isSuccess());
    UUID section7Id = section7Result.getSuccessData().getId();

    // 현재 구조 확인 - 루트 > 2장 > 2.A 상세분석 > 섹션
    toc = editTocUseCase.getToc(bookId);
    assertTocStructure(toc, 4); // 루트(1) > 2장(2) > 2.A(3) > 섹션(4) 이므로 깊이 4
    assertNodeChildrenCount(toc, subFolder1Id, 2);

    // 6. 3단계 중첩 폴더 및 섹션 생성
    Result<FolderDto> subSubFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user1.getUid(),
        bookId,
        subFolder1Id,
        "2.A.X 특별 사례"
      )
    );
    Assertions.assertTrue(subSubFolderResult.isSuccess());
    UUID subSubFolderId = subSubFolderResult.getSuccessData().getId();

    Result<SectionDtoWithContent> section8Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user1.getUid(),
        bookId,
        subSubFolderId,
        "2.A.X.1 특별 케이스"
      )
    );
    Assertions.assertTrue(section8Result.isSuccess());
    UUID section8Id = section8Result.getSuccessData().getId();

    // 현재 구조 확인 - 루트 > 2장 > 2.A > 2.A.X > 섹션
    toc = editTocUseCase.getToc(bookId);
    assertTocStructure(toc, 5); // 루트(1) > 2장(2) > 2.A(3) > 2.A.X(4) > 섹션(5) 이므로 깊이 5
    assertNodeChildrenCount(toc, subSubFolderId, 1);

    // 7. 노드 이동 테스트 - 같은 레벨에서 순서 변경
    // 2장 내의 섹션들 순서 변경: 2.2, 2.3, 2.1 순서로 변경
    Result moveSection2Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        section2Id,
        folder2Id,
        2
      )
    );
    Assertions.assertTrue(moveSection2Result.isSuccess());

    // 순서 확인
    toc = editTocUseCase.getToc(bookId);
    assertNodeOrder(toc, folder2Id, Arrays.asList(section3Id, section4Id, section2Id, subFolder1Id));
    assertTocStructure(toc, 5); // 구조 깊이는 여전히 5

    // 8. 노드 이동 테스트 - 다른 부모로 이동
    // 2.A.X.1 섹션을 1장으로 이동
    Result moveSection8Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        section8Id,
        folder1Id,
        1
      )
    );
    Assertions.assertTrue(moveSection8Result.isSuccess());

    // 이동 확인
    toc = editTocUseCase.getToc(bookId);
    assertNodeParent(toc, section8Id, folder1Id);
    assertNodeChildrenCount(toc, folder1Id, 2);
    assertNodeChildrenCount(toc, subSubFolderId, 0);
    assertTocStructure(toc, 4); // 최대 깊이가 4로 감소 (2.A.X 폴더 하위에 2.A.X.1가 1장으로 이동하여 유일한 최하위 노드가 없어짐.)

    // 9. 계층 이동 - 폴더를 상위로 이동
    // 2.A 폴더를 루트로 이동
    Result moveSubFolder1Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        subFolder1Id,
        rootFolderId,
        0
      )
    );
    Assertions.assertTrue(moveSubFolder1Result.isSuccess());

    toc = editTocUseCase.getToc(bookId);
    assertNodeParent(toc, subFolder1Id, rootFolderId);
    assertNodeChildrenCount(toc, folder2Id, 3); // 섹션 3개만 남음
    assertTocStructure(toc, 3); // 이제 최대 깊이는 3; 루트(1) > 2.A(2) > 2.A.X(3, 비어있음)

    // 10. 계층 이동 - 폴더를 하위 레벨로 이동
    // 3장을 2.A 폴더 안으로 이동
    Result moveFolder3Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        folder3Id,
        subFolder1Id,
        0
      )
    );
    Assertions.assertTrue(moveFolder3Result.isSuccess());

    toc = editTocUseCase.getToc(bookId);
    assertNodeParent(toc, folder3Id, subFolder1Id);
    assertChildCount(toc.getRoot(), 3); // 루트 아래 1장, 2장, 2.A
    assertTocStructure(toc, 4); // 최대 깊이는 4; root > 2.A > 3장 > 3.1

    // 11. 섹션 업데이트 테스트
    String updatedTitle = "업데이트된, 섹션 제목";
    String updatedContent = "이것은 업데이트된 내용입니다.";

    // 섹션 제목 변경
    NodeIdentifier section1Identifier = new NodeIdentifier(
      user1.getUid(),
      bookId,
      section1Id
    );
    Result<SectionDto> updateSectionTitleResult = editTocUseCase.changeSectionTitle(section1Identifier, updatedTitle);
    Assertions.assertTrue(updateSectionTitleResult.isSuccess());

    // 섹션 내용 변경
    Result<SectionDtoWithContent> updateSectionContentResult = sectionWriteUseCase.writeContent(section1Identifier, updatedContent);
    Assertions.assertTrue(updateSectionContentResult.isSuccess());

    // 섹션 조회하여 확인
    Result<SectionDtoWithContent> sectionWithContentResult = sectionWriteUseCase.getSectionWithContent(section1Identifier);
    Assertions.assertTrue(sectionWithContentResult.isSuccess());
    SectionDtoWithContent updatedSection = sectionWithContentResult.getSuccessData();
    Assertions.assertEquals(updatedTitle, updatedSection.getTitle());
    Assertions.assertEquals(updatedContent, updatedSection.getContent());

    // 12. 폴더 업데이트 테스트
    String updatedFolderTitle = "2장. 수정된 본론";
    NodeIdentifier folder2Identifier = new NodeIdentifier(
      user1.getUid(),
      bookId,
      folder2Id
    );
    Result<FolderDto> updateFolderResult = editTocUseCase.changeFolderTitle(folder2Identifier, updatedFolderTitle);
    Assertions.assertTrue(updateFolderResult.isSuccess());

    // 폴더 조회하여 확인
    Result<FolderDto> folderResult = editTocUseCase.getFolder(folder2Identifier);
    Assertions.assertTrue(folderResult.isSuccess());
    Assertions.assertEquals(updatedFolderTitle, folderResult.getSuccessData().getTitle());

    // 13. 폴더 삭제 테스트 (하위 구조를 포함한 폴더, 2.A)
    Result deleteSubFolder1Result = editTocUseCase.deleteFolder(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        subFolder1Id
      )
    );
    Assertions.assertTrue(deleteSubFolder1Result.isSuccess());

    // 삭제 후 구조 확인
    toc = editTocUseCase.getToc(bookId);
    assertNodeExists(toc, subFolder1Id, false); // 삭제된 폴더
    assertNodeExists(toc, folder3Id, false); // 삭제된 폴더의 하위 폴더도 삭제됨
    assertNodeExists(toc, subSubFolderId, false); // 2.A.X 폴더도 삭제됨
    assertTocStructure(toc, 3); // 이제 최대 깊이는 3; root > 1, 2장 > 섹션들

    // 14. 섹션 삭제 테스트(2.1)
    Result deleteSection2Result = editTocUseCase.deleteSection(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        section2Id
      )
    );
    Assertions.assertTrue(deleteSection2Result.isSuccess());

    // 섹션 삭제 확인
    toc = editTocUseCase.getToc(bookId);
    assertNodeExists(toc, section2Id, false);
    assertNodeChildrenCount(toc, folder2Id, 2); // 남은 섹션 2개

    // 15. 극단적인 계층 이동 - 모두 root로 이동시켜서 계층 구조 평탄화
    List<UUID> remainingSections = new ArrayList<>();
    remainingSections.add(section1Id);
    remainingSections.add(section8Id);
    remainingSections.add(section3Id);
    remainingSections.add(section4Id);

    for(int i = 0; i < remainingSections.size(); i++) {
      Result moveSectionResult = editTocUseCase.relocateNode(
        new RelocateNodeCmd(
          user1.getUid(),
          bookId,
          remainingSections.get(i),
          rootFolderId,
          i + 2
        )
      );
      Assertions.assertTrue(moveSectionResult.isSuccess());
    }

    // 모든 노드가 루트 아래로 이동되었는지 확인
    toc = editTocUseCase.getToc(bookId);
    for(UUID sectionId : remainingSections) {
      assertNodeParent(toc, sectionId, rootFolderId);
    }
    assertTocStructure(toc, 2); // 모두 루트 직접 아래로 이동했으므로 최대 깊이 2 (루트(1) > 섹션/폴더(2))

    // 16. 남은 모든 폴더 삭제 확인
    Result deleteFolder1Result = editTocUseCase.deleteFolder(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        folder1Id
      )
    );
    Assertions.assertTrue(deleteFolder1Result.isSuccess());

    Result deleteFolder2Result = editTocUseCase.deleteFolder(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        folder2Id
      )
    );
    Assertions.assertTrue(deleteFolder2Result.isSuccess());

    // 최종 구조 확인
    toc = editTocUseCase.getToc(bookId);
    assertNodeExists(toc, folder1Id, false);
    assertNodeExists(toc, folder2Id, false);
    assertTocStructure(toc, 2); // 루트(1) > 섹션(2)이므로 깊이 2

    // 남은 섹션들만 있는지 확인
    List<TocDto.Node> rootChildren = toc.getRoot().getChildren();
    for(TocDto.Node child : rootChildren) {
      if(child.getType() == TocNodeType.SECTION) {
        boolean found = false;
        for(UUID sectionId : remainingSections) {
          if(sectionId.equals(child.getId())) {
            found = true;
            break;
          }
        }
        Assertions.assertTrue(found, "예상치 못한 섹션이 존재합니다: " + child.getId());
      }
    }
  }

  @Test
  @DisplayName("부모 폴더를 자식 폴더로 이동 시 계층 구조 위반 검증")
  void parentToChildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "계층 구조 위반 테스트 - 부모->자식 이동");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto.Toc toc = editTocUseCase.getToc(bookId);
    UUID rootFolderId = toc.getRoot().getId();

    // 3. 폴더 계층 구조 생성: 루트 > 부모 > 자식
    Result<FolderDto> parentFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "부모 폴더"
      )
    );
    Assertions.assertTrue(parentFolderResult.isSuccess());
    UUID parentFolderId = parentFolderResult.getSuccessData().getId();

    Result<FolderDto> childFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        parentFolderId,
        "자식 폴더"
      )
    );
    Assertions.assertTrue(childFolderResult.isSuccess());
    UUID childFolderId = childFolderResult.getSuccessData().getId();

    // 4. 부모 폴더를 자식 폴더로 이동 시도 (계층 구조 위반)
    Result moveResult = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        parentFolderId,
        childFolderId,
        0
      )
    );

    // 5. 계층 구조 위반으로 실패 검증
    Assertions.assertTrue(moveResult.is(BookCode.TOC_HIERARCHY_ERROR));
  }

  @Test
  @DisplayName("부모 폴더를 손자 폴더로 이동 시 계층 구조 위반 검증")
  void parentToGrandchildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "계층 구조 위반 테스트 - 부모->손자 이동");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto.Toc toc = editTocUseCase.getToc(bookId);
    UUID rootFolderId = toc.getRoot().getId();

    // 3. 3단계 폴더 계층 구조 생성: 루트 > 부모 > 자식 > 손자

    Result<FolderDto> parentFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "부모 폴더"
      )
    );
    Assertions.assertTrue(parentFolderResult.isSuccess());
    UUID parentFolderId = parentFolderResult.getSuccessData().getId();

    Result<FolderDto> childFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        parentFolderId,
        "자식 폴더"
      )
    );
    Assertions.assertTrue(childFolderResult.isSuccess());
    UUID childFolderId = childFolderResult.getSuccessData().getId();

    Result<FolderDto> grandchildFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        childFolderId,
        "손자 폴더"
      )
    );
    Assertions.assertTrue(grandchildFolderResult.isSuccess());
    UUID grandchildFolderId = grandchildFolderResult.getSuccessData().getId();

    // 4. 부모 폴더를 손자 폴더로 이동 시도 (계층 구조 위반)
    Result moveResult = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        parentFolderId,
        grandchildFolderId,
        0
      )
    );

    // 5. 계층 구조 위반으로 실패 검증
    Assertions.assertTrue(moveResult.is(BookCode.TOC_HIERARCHY_ERROR));
  }

  @Test
  @DisplayName("루트 폴더 이동 시도 시 실패 검증")
  void rootFolderMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "루트 폴더 이동 시도 테스트");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto.Toc toc = editTocUseCase.getToc(bookId);
    UUID rootFolderId = toc.getRoot().getId();

    // 3. 일반 폴더 생성
    Result<FolderDto> normalFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "일반 폴더"
      )
    );
    Assertions.assertTrue(normalFolderResult.isSuccess());
    UUID normalFolderId = normalFolderResult.getSuccessData().getId();

    // 4. 루트 폴더를 일반 폴더로 이동 시도
    Result moveResult = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        normalFolderId,
        0
      )
    );

    // 5. 루트 폴더 이동 불가 검증
    Assertions.assertTrue(moveResult.is(BookCode.TOC_HIERARCHY_ERROR));
  }

  @Test
  @DisplayName("폴더가 자기 자신을 부모로 지정 시도 시 실패 검증")
  void selfParentTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "자기 자신 부모 지정 테스트");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto.Toc toc = editTocUseCase.getToc(bookId);
    UUID rootFolderId = toc.getRoot().getId();

    // 3. 일반 폴더 생성
    Result<FolderDto> folderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더 A"
      )
    );
    Assertions.assertTrue(folderResult.isSuccess());
    UUID folderId = folderResult.getSuccessData().getId();

    // 4. 폴더가 자기 자신을 부모로 지정 시도
    Result moveResult = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folderId,
        folderId,
        0
      )
    );

    // 5. 자기 자신을 부모로 지정 불가 검증
    Assertions.assertTrue(moveResult.is(BookCode.TOC_HIERARCHY_ERROR));
  }

  @Test
  @SuppressWarnings("OverlyLongMethod")
  @DisplayName("복잡한 계층 구조에서 순환 참조 시도 시 실패 검증")
  void complexCyclicReferenceTest() {
    // 1. 테스트 셋업 및
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "복잡한 계층 구조 순환 참조 테스트");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto.Toc toc = editTocUseCase.getToc(bookId);
    UUID rootFolderId = toc.getRoot().getId();

    // 3. 여러 계층의 폴더 구조 생성
    // 루트 > A > B > C > D
    Result<FolderDto> folderAResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더 A"
      )
    );
    Assertions.assertTrue(folderAResult.isSuccess());
    UUID folderAId = folderAResult.getSuccessData().getId();

    Result<FolderDto> folderBResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        folderAId,
        "폴더 B"
      )
    );
    Assertions.assertTrue(folderBResult.isSuccess());
    UUID folderBId = folderBResult.getSuccessData().getId();

    Result<FolderDto> folderCResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        folderBId,
        "폴더 C"
      )
    );
    Assertions.assertTrue(folderCResult.isSuccess());
    UUID folderCId = folderCResult.getSuccessData().getId();

    Result<FolderDto> folderDResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        folderCId,
        "폴더 D"
      )
    );
    Assertions.assertTrue(folderDResult.isSuccess());
    UUID folderDId = folderDResult.getSuccessData().getId();

    // 루트 > E > F
    Result<FolderDto> folderEResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더 E"
      )
    );
    Assertions.assertTrue(folderEResult.isSuccess());
    UUID folderEId = folderEResult.getSuccessData().getId();

    Result<FolderDto> folderFResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        folderEId,
        "폴더 F"
      )
    );
    Assertions.assertTrue(folderFResult.isSuccess());
    UUID folderFId = folderFResult.getSuccessData().getId();

    // 4. 다양한 순환 참조 시도
    // 4.1. A를 D로 이동 시도 (A > B > C > D > A 순환 발생)
    Result moveResAtoD = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folderAId,
        folderDId,
        0
      )
    );
    Assertions.assertTrue(moveResAtoD.is(BookCode.TOC_HIERARCHY_ERROR));

    // 4.2. E를 F로 이동 시도 (E > F > E 순환 발생)
    Result moveResEtoF = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folderEId,
        folderFId,
        0
      )
    );
    Assertions.assertTrue(moveResEtoF.is(BookCode.TOC_HIERARCHY_ERROR));

    // 5. 유효한 이동 확인 (F를 C로 이동 - 순환 참조 없음)
    Result validMove = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folderFId,
        folderCId,
        0
      )
    );
    Assertions.assertTrue(validMove.isSuccess());

    // 구조 확인: 루트 > A > B > C > F, D
    toc = editTocUseCase.getToc(bookId);
    assertNodeParent(toc, folderFId, folderCId);
  }

  // 이하 검증 유틸리티 메소드들

  /**
   * 목차 구조의 최대 깊이 검증
   */
  private void assertTocStructure(TocDto.Toc toc, int expectedDepth) {
    int actualDepth = calculateMaxDepth(toc.getRoot());
    Assertions.assertEquals(expectedDepth, actualDepth, "목차 구조의 최대 깊이가 예상과 다릅니다");
  }

  /**
   * 노드의 최대 깊이 계산
   */
  private int calculateMaxDepth(TocDto.Node node) {
    if(node == null) {
      return 0;
    }

    int maxDepth = 1; // 현재 노드 자체를 포함

    if(node instanceof TocDto.Folder folder && folder.getChildren() != null && !folder.getChildren().isEmpty()) {
      int childMaxDepth = 0;

      for(TocDto.Node child : folder.getChildren()) {
        int depth = calculateMaxDepth(child);
        if(depth > childMaxDepth) {
          childMaxDepth = depth;
        }
      }

      maxDepth += childMaxDepth;
    }

    return maxDepth;
  }

  /**
   * 노드의 자식 수 검증
   */
  private void assertNodeChildrenCount(TocDto.Toc toc, UUID nodeId, int expectedCount) {
    TocDto.Node node = findNodeById(toc.getRoot(), nodeId);
    Assertions.assertNotNull(node, "ID가 " + nodeId + "인 노드를 찾을 수 없습니다");

    if(node instanceof TocDto.Folder folder) {
      Assertions.assertEquals(expectedCount, folder.getChildren().size(),
        "ID가 " + nodeId + "인 노드의 자식 수가 예상과 다릅니다");
    } else {
      Assertions.assertEquals(0, expectedCount,
        "ID가 " + nodeId + "인 노드에 자식이 없어야 합니다");
    }
  }

  /**
   * 루트 노드의 직접 자식 수 검증
   */
  private void assertChildCount(TocDto.Folder rootNode, int expectedCount) {
    Assertions.assertEquals(expectedCount, rootNode.getChildren().size(),
      "루트 노드의 자식 수가 예상과 다릅니다");
  }

  /**
   * 노드의 부모 검증
   */
  private void assertNodeParent(TocDto.Toc toc, UUID nodeId, UUID expectedParentId) {
    String actualParentId = getParentId(toc.getRoot(), nodeId);
    Assertions.assertEquals(expectedParentId.toString(), actualParentId,
      "ID가 " + nodeId + "인 노드의 부모가 예상과 다릅니다");
  }

  /**
   * 노드의 존재 여부 검증
   */
  private void assertNodeExists(TocDto.Toc toc, UUID nodeId, boolean shouldExist) {
    TocDto.Node node = findNodeById(toc.getRoot(), nodeId);
    if(shouldExist) {
      Assertions.assertNotNull(node, "ID가 " + nodeId + "인 노드가 존재해야 합니다");
    } else {
      Assertions.assertNull(node, "ID가 " + nodeId + "인 노드가 존재하지 않아야 합니다");
    }
  }

  /**
   * 노드의 순서 검증
   */
  private void assertNodeOrder(TocDto.Toc toc, UUID parentId, List<UUID> expectedOrder) {
    TocDto.Node parentNode = findNodeById(toc.getRoot(), parentId);
    Assertions.assertNotNull(parentNode, "ID가 " + parentId + "인 부모 노드를 찾을 수 없습니다");
    Assertions.assertInstanceOf(TocDto.Folder.class, parentNode, "노드는 폴더여야 합니다");

    TocDto.Folder folder = (TocDto.Folder) parentNode;
    List<TocDto.Node> children = folder.getChildren();
    Assertions.assertEquals(expectedOrder.size(), children.size(),
      "ID가 " + parentId + "인 부모 노드의 자식 수가 예상과 다릅니다");

    for(int i = 0; i < expectedOrder.size(); i++) {
      UUID expectedId = expectedOrder.get(i);
      UUID actualId = children.get(i).getId();
      Assertions.assertEquals(expectedId, actualId,
        "인덱스 " + i + "의 자식 노드 ID가 예상과 다릅니다");
    }
  }

  /**
   * ID로 노드 검색
   */
  private TocDto.Node findNodeById(TocDto.Node node, UUID id) {
    if(node == null) {
      return null;
    }

    if(node.getId().equals(id)) {
      return node;
    }

    if(node instanceof TocDto.Folder folder) {
      for(TocDto.Node child : folder.getChildren()) {
        TocDto.Node foundNode = findNodeById(child, id);
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
  private String getParentId(TocDto.Node node, UUID childId) {
    if(node == null) {
      return null;
    }

    if(node instanceof TocDto.Folder folder) {
      for(TocDto.Node child : folder.getChildren()) {
        if(child.getId().equals(childId)) {
          return node.getId().toString();
        }

        String foundParentId = getParentId(child, childId);
        if(foundParentId != null) {
          return foundParentId;
        }
      }
    }

    return null;
  }
}