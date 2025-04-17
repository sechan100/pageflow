package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.TocNodePersistencePort;
import org.pageflow.book.persistence.toc.TocPersistencePort;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.SectionWriteUseCase;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.book.usecase.cmd.RelocateNodeCmd;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.book.utils.TocUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import java.util.ArrayList;
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
  private final TocUtils tocUtils;
  private final EditTocUseCase editTocUseCase;
  private final SectionWriteUseCase sectionWriteUseCase;
  private final BookPersistencePort bookPersistencePort;
  private final TocPersistencePort tocPersistencePort;
  private final TocNodePersistencePort nodePersistencePort;


  @SuppressWarnings("OverlyLongMethod")
  @Test
  @DisplayName("목차 계층 구조 생성 및 이동 복합 테스트")
  void tocComplexStructureTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user1 = userUtils.createUser("user1");
    BookDto bookDto = bookUtils.createBook(user1, "목차 구조 테스트 도서");
    UUID bookId = bookDto.getId();

    tocUtils.buildTree(bookDto)
      .folder("f1", f -> f
        .section("s11")
      )
      .folder("f2", f -> f
        .section("s21")
        .section("s22")
        .section("s23")
        .folder("f24", sf -> sf
          .section("s241")
          .section("s242")
          .folder("f243", ssf -> ssf
            .section("s2431")
          )
        )
      )
      .folder("f3", f -> f
        .section("s31")
      );

    Book book = bookPersistencePort.findById(bookId).get();
    Toc toc = tocPersistencePort.loadEditableToc(book);

    UUID rootFolderId = toc.getRootFolder().getId();
    UUID f1Id = toc.getRootFolder().getChildren().get(0).getId();
    UUID s11Id = ((TocFolder) toc.get(f1Id)).getChildren().get(0).getId();
    UUID f2Id = toc.getRootFolder().getChildren().get(1).getId();
    UUID s21Id = ((TocFolder) toc.get(f2Id)).getChildren().get(0).getId();
    UUID s22Id = ((TocFolder) toc.get(f2Id)).getChildren().get(1).getId();
    UUID s23Id = ((TocFolder) toc.get(f2Id)).getChildren().get(2).getId();
    UUID f24Id = ((TocFolder) toc.get(f2Id)).getChildren().get(3).getId();
    UUID s241Id = ((TocFolder) toc.get(f24Id)).getChildren().get(0).getId();
    UUID s242Id = ((TocFolder) toc.get(f24Id)).getChildren().get(1).getId();
    UUID f243Id = ((TocFolder) toc.get(f24Id)).getChildren().get(2).getId();
    UUID s2431Id = ((TocFolder) toc.get(f243Id)).getChildren().get(0).getId();
    UUID f3Id = toc.getRootFolder().getChildren().get(2).getId();
    UUID s31Id = ((TocFolder) toc.get(f3Id)).getChildren().get(0).getId();

    // reorder f2_1
    tocUtils.assertChildrenStructure(f2Id, s21Id, s22Id, s23Id, f24Id);
    Result moveSection2Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        s21Id,
        f2Id,
        2
      )
    );
    Assertions.assertTrue(moveSection2Result.isSuccess());
    tocUtils.assertChildrenStructure(f2Id, s22Id, s23Id, s21Id, f24Id);
    tocUtils.assertFolderDepth(rootFolderId, 5);

    // repaint f2_4_3
    tocUtils.assertChildrenStructure(f243Id, s2431Id);
    tocUtils.assertChildrenStructure(f1Id, s11Id);
    Result moveSection8Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        s2431Id,
        f1Id,
        1
      )
    );
    Assertions.assertTrue(moveSection8Result.isSuccess());
    tocUtils.assertChildrenStructure(f243Id, new UUID[]{});
    tocUtils.assertChildrenStructure(f1Id, s11Id, s2431Id);
    tocUtils.assertFolderDepth(rootFolderId, 4); // 최대 깊이가 4로 감소 (2.A.X 폴더 하위에 2.A.X.1가 1장으로 이동하여 유일한 최하위 노드가 없어짐.)

    // repaint f24Id
    tocUtils.assertChildrenStructure(f2Id, s22Id, s23Id, s21Id, f24Id);
    tocUtils.assertChildrenStructure(rootFolderId, f1Id, f2Id, f3Id);
    Result moveSubFolder1Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        f24Id,
        rootFolderId,
        0
      )
    );
    Assertions.assertTrue(moveSubFolder1Result.isSuccess());
    tocUtils.assertChildrenStructure(f2Id, s22Id, s23Id, s21Id);
    tocUtils.assertChildrenStructure(rootFolderId, f24Id, f1Id, f2Id, f3Id);
    tocUtils.assertFolderDepth(rootFolderId, 3); // 이제 최대 깊이는 3; 루트(1) > 2.A(2) > 2.A.X(3, 비어있음)

    // repaint f3
    tocUtils.assertChildrenStructure(rootFolderId, f24Id, f1Id, f2Id, f3Id);
    tocUtils.assertChildrenStructure(f24Id, s241Id, s242Id, f243Id);
    Result moveFolder3Result = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user1.getUid(),
        bookId,
        f3Id,
        f24Id,
        0
      )
    );
    Assertions.assertTrue(moveFolder3Result.isSuccess());
    tocUtils.assertChildrenStructure(rootFolderId, f24Id, f1Id, f2Id);
    tocUtils.assertChildrenStructure(f24Id, f3Id, s241Id, s242Id, f243Id);
    tocUtils.assertFolderDepth(rootFolderId, 4); // 최대 깊이는 4; root > 2.A > 3장 > 3.1

    // 섹션 업데이트 테스트
    String updatedTitle = "업데이트된, 섹션 제목";
    String updatedContent = "이것은 업데이트된 내용입니다.";
    NodeIdentifier section1Identifier = new NodeIdentifier(
      user1.getUid(),
      bookId,
      s11Id
    );
    Result<SectionDto> updateSectionTitleResult = editTocUseCase.changeSectionTitle(section1Identifier, updatedTitle);
    Assertions.assertTrue(updateSectionTitleResult.isSuccess());
    // 섹션 내용 변경
    Result<WithContentSectionDto> updateSectionContentResult = sectionWriteUseCase.writeContent(section1Identifier, updatedContent);
    Assertions.assertTrue(updateSectionContentResult.isSuccess());
    // 섹션 조회하여 확인
    Result<WithContentSectionDto> sectionWithContentResult = sectionWriteUseCase.getSectionWithContent(section1Identifier);
    Assertions.assertTrue(sectionWithContentResult.isSuccess());
    WithContentSectionDto updatedSection = sectionWithContentResult.get();
    Assertions.assertEquals(updatedTitle, updatedSection.getTitle());
    Assertions.assertEquals(updatedContent, updatedSection.getContent());

    // 12. 폴더 업데이트 테스트
    String updatedFolderTitle = "2장. 수정된 본론";
    NodeIdentifier folder2Identifier = new NodeIdentifier(
      user1.getUid(),
      bookId,
      f2Id
    );
    Result<FolderDto> updateFolderResult = editTocUseCase.changeFolderTitle(folder2Identifier, updatedFolderTitle);
    Assertions.assertTrue(updateFolderResult.isSuccess());

    // 폴더 조회하여 확인
    Result<FolderDto> folderResult = editTocUseCase.getFolder(folder2Identifier);
    Assertions.assertTrue(folderResult.isSuccess());
    Assertions.assertEquals(updatedFolderTitle, folderResult.get().getTitle());

    // 13. 폴더 삭제 테스트 (하위 구조를 포함한 폴더, f24Id 삭제)
    Result deleteSubFolder1Result = editTocUseCase.deleteFolder(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        f24Id
      )
    );
    Assertions.assertTrue(deleteSubFolder1Result.isSuccess());
    tocUtils.assertChildrenStructure(rootFolderId, f1Id, f2Id);
    tocUtils.assertFolderDepth(rootFolderId, 3); // 이제 최대 깊이는 3; root > 1, 2장 > 섹션들

    // 14. 섹션 삭제 테스트(2.1)
    Result deleteSection2Result = editTocUseCase.deleteSection(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        s21Id
      )
    );
    Assertions.assertTrue(deleteSection2Result.isSuccess());
    tocUtils.assertChildrenStructure(f2Id, s22Id, s23Id);

    // 모두 root로 이동시켜서 계층 구조 평탄화
    List<UUID> remainingSections = new ArrayList<>();
    remainingSections.add(s11Id);
    remainingSections.add(s2431Id);
    remainingSections.add(s22Id);
    remainingSections.add(s23Id);

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
    tocUtils.assertChildrenStructure(rootFolderId, f1Id, f2Id, s11Id, s2431Id, s22Id, s23Id);
    tocUtils.assertFolderDepth(rootFolderId, 2); // 모두 루트 직접 아래로 이동했으므로 최대 깊이 2 (루트(1) > 섹션/폴더(2))

    // 16. 남은 모든 폴더 삭제
    Result deleteFolder1Result = editTocUseCase.deleteFolder(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        f1Id
      )
    );
    Assertions.assertTrue(deleteFolder1Result.isSuccess());

    Result deleteFolder2Result = editTocUseCase.deleteFolder(
      new NodeIdentifier(
        user1.getUid(),
        bookId,
        f2Id
      )
    );
    Assertions.assertTrue(deleteFolder2Result.isSuccess());

    // 최종 구조 확인
    tocUtils.assertChildrenStructure(rootFolderId, s11Id, s2431Id, s22Id, s23Id);
    tocUtils.assertFolderDepth(rootFolderId, 2); // 루트(1) > 섹션(2)이므로 깊이 2
  }

  @Test
  @DisplayName("부모 폴더를 자식 폴더로 이동 시 계층 구조 위반 검증")
  void parentToChildMoveTest() {
    // 1. 테스트 셋업 및 책 생성
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "계층 구조 위반 테스트 - 부모->자식 이동");
    UUID bookId = book.getId();

    // 2. 기본 루트 폴더 확인
    TocDto toc = editTocUseCase.getToc(user.getUid(), bookId).get();
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
    UUID parentFolderId = parentFolderResult.get().getId();

    Result<FolderDto> childFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        parentFolderId,
        "자식 폴더"
      )
    );
    Assertions.assertTrue(childFolderResult.isSuccess());
    UUID childFolderId = childFolderResult.get().getId();

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
    TocDto toc = editTocUseCase.getToc(user.getUid(), bookId).get();
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
    UUID parentFolderId = parentFolderResult.get().getId();

    Result<FolderDto> childFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        parentFolderId,
        "자식 폴더"
      )
    );
    Assertions.assertTrue(childFolderResult.isSuccess());
    UUID childFolderId = childFolderResult.get().getId();

    Result<FolderDto> grandchildFolderResult = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        childFolderId,
        "손자 폴더"
      )
    );
    Assertions.assertTrue(grandchildFolderResult.isSuccess());
    UUID grandchildFolderId = grandchildFolderResult.get().getId();

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
    TocDto toc = editTocUseCase.getToc(user.getUid(), bookId).get();
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
    UUID normalFolderId = normalFolderResult.get().getId();

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
    TocDto toc = editTocUseCase.getToc(user.getUid(), bookId).get();
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
    UUID folderId = folderResult.get().getId();

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
    Assertions.assertFalse(moveResult.isSuccess());
  }

  @Test
  @DisplayName("복잡한 계층 구조에서 순환 참조 시도 시 실패 검증")
  void complexCyclicReferenceTest() {
    // 1. 테스트 셋업 및
    UserDto user = userUtils.createUser("user1");
    UUID bookId = bookUtils.createBook(user, "복잡한 계층 구조 순환 참조 테스트").getId();

    // 2. 기본 루트 폴더 확인
    Book book = bookPersistencePort.findById(bookId).get();
    Toc toc = tocPersistencePort.loadEditableToc(book);
    TocFolder rootFolder = toc.getRootFolder();
    UUID rootFolderId = toc.getRootFolder().getId();
    tocUtils.buildTree(user.getUid(), bookId)
      .folder("폴더 1", f ->
        f.folder("폴더 1-1", ff ->
          ff.folder("폴더 1-1-1")
        )
      )
      .folder("폴더 2")
      .folder("폴더 3")
      .folder("폴더 4")
      .folder("폴더 5", f ->
        f.folder("폴더 5-1")
      )
    ;
    UUID f1Id = toc.getRootFolder().getChildren().get(0).getId();
    UUID f11Id = ((TocFolder) toc.get(f1Id)).getChildren().get(0).getId();
    UUID f111Id = ((TocFolder) toc.get(f11Id)).getChildren().get(0).getId();
    UUID f2Id = toc.getRootFolder().getChildren().get(1).getId();
    UUID f3Id = toc.getRootFolder().getChildren().get(2).getId();
    UUID f4Id = toc.getRootFolder().getChildren().get(3).getId();
    UUID f5Id = toc.getRootFolder().getChildren().get(4).getId();
    UUID f51Id = ((TocFolder) toc.get(f5Id)).getChildren().get(0).getId();

    // 폴더 1 -> 폴더 1-1-1로 이동 시도
    Result moveResAtoD = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        f1Id,
        f111Id,
        0
      )
    );
    Assertions.assertTrue(moveResAtoD.is(BookCode.TOC_HIERARCHY_ERROR));

    // 폴더 5 -> 폴더 5-1로 이동 시도
    Result moveResEtoF = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        f5Id,
        f51Id,
        0
      )
    );
    Assertions.assertTrue(moveResEtoF.is(BookCode.TOC_HIERARCHY_ERROR));

    // 폴더 5-1 -> 폴더 1-1로 이동 시도
    System.out.println("============================================================================================================================================================================================================");
    tocUtils.assertChildrenStructure(f5Id, f51Id);
    tocUtils.assertChildrenStructure(f11Id, f111Id);
    Result validMove = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        f51Id,
        f11Id,
        0
      )
    );
    nodePersistencePort.flush();
    System.out.println("============================================================================================================================================================================================================");
    Assertions.assertTrue(validMove.isSuccess());
    tocUtils.assertChildrenStructure(f5Id);
    tocUtils.assertChildrenStructure(f11Id, f51Id, f111Id);
  }

}