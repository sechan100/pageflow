package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.adapter.out.TocNodeJpaRepository;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.FolderDto;
import org.pageflow.book.application.dto.SectionDtoWithContent;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PageflowTest
@RequiredArgsConstructor
public class TocFeatureTest {
  private final UserUtils userUtils;
  private final BookUtils bookUtils;
  private final EditTocUseCase editTocUseCase;
  private final TocNodeJpaRepository nodePersistencePort;

  @Test
  @DisplayName("ov 값 할당과 rebalance 검사")
  void ovAssignAndRebalanceTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성
    BookDto book = bookUtils.createBook(user, "TOC 테스트 도서");

    // toc 가져오기
    UUID bookId = book.getId();
    UUID rootFolderId = editTocUseCase.getToc(user.getUid(), bookId).getSuccessData().getRoot().getId();

    // 폴더 생성
    Result<FolderDto> folder1Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더1"
      )
    );
    assertTrue(folder1Result.isSuccess());
    FolderDto folder1 = folder1Result.getSuccessData();

    Result<FolderDto> folder2Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더2"
      )
    );
    assertTrue(folder2Result.isSuccess());
    FolderDto folder2 = folder2Result.getSuccessData();

    // 섹션 생성
    Result<SectionDtoWithContent> section1Result = editTocUseCase.createSection(
      new CreateSectionCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "섹션1"
      )
    );
    assertTrue(section1Result.isSuccess());
    SectionDtoWithContent section1 = section1Result.getSuccessData();

    // 엔티티 load 및 ov 검증
    TocNode en_f1 = nodePersistencePort.findById(folder1.getId()).get();
    assertEquals(0, en_f1.getOv());

    TocNode en_f2 = nodePersistencePort.findById(folder2.getId()).get();
    TocNode en_s1 = nodePersistencePort.findById(section1.getId()).get();

    // ov 강제 할당
    en_f2.setOv(100);
    en_s1.setOv(101);
    nodePersistencePort.flush();
    assertEquals(
      100,
      nodePersistencePort.findById(folder2.getId()).get().getOv()
    );

    // rebalance - 폴더1을 인덱스 1로 이동
    Result relocateResult = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folder1.getId(),
        rootFolderId,
        1
      )
    );
    assertTrue(relocateResult.isSuccess());

    // 엔티티 load 및 ov 재검증
    int afterRebalance_f2Ov = nodePersistencePort.findById(folder2.getId()).get().getOv();
    int afterRebalance_f1Ov = nodePersistencePort.findById(folder1.getId()).get().getOv();
    int afterRebalance_s1Ov = nodePersistencePort.findById(section1.getId()).get().getOv();

    // OV가 rebalance되었는지 확인
    assertTrue(afterRebalance_f2Ov < afterRebalance_f1Ov);
    assertTrue(afterRebalance_f1Ov < afterRebalance_s1Ov);
  }

  @Test
  @DisplayName("int minimum value에 도달한 경우 rebalance 검사")
  void minimumOvRebalanceTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성
    BookDto book = bookUtils.createBook(user, "최소값 rebalance 테스트");
    UUID bookId = book.getId();
    UUID rootFolderId = editTocUseCase.getToc(user.getUid(), bookId).getSuccessData().getRoot().getId();

    // 노드 생성
    Result<FolderDto> folder1Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더1"
      )
    );
    FolderDto folder1 = folder1Result.getSuccessData();

    Result<FolderDto> folder2Result = editTocUseCase.createFolder(
      new CreateFolderCmd(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더2"
      )
    );
    FolderDto folder2 = folder2Result.getSuccessData();

    // 최소값 근처에 ov 설정
    TocNode en_f1 = nodePersistencePort.findById(folder1.getId()).get();
    en_f1.setOv(Integer.MIN_VALUE + 1);
    nodePersistencePort.flush();

    // folder2를 맨 앞으로 이동시켜 rebalance 유도
    Result relocateResult = editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folder2.getId(),
        rootFolderId,
        0
      )
    );
    assertTrue(relocateResult.isSuccess());

    // rebalance 확인 - 모든 ov가 재조정되었는지 확인
    int newF1Ov = nodePersistencePort.findById(folder1.getId()).get().getOv();
    int newF2Ov = nodePersistencePort.findById(folder2.getId()).get().getOv();

    assertTrue(newF2Ov < newF1Ov); // folder2가 folder1 앞에 있어야 함
    assertTrue(newF2Ov > Integer.MIN_VALUE + 1000); // rebalance 후 최소값에서 충분히 떨어져 있어야 함
  }
}