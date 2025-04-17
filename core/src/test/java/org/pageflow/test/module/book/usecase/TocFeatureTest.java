package org.pageflow.test.module.book.usecase;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.TocRepository;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.RelocateNodeCmd;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.book.utils.TocUtils;
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
  private final TocUtils tocUtils;
  private final BookPersistencePort bookPersistencePort;
  private final TocRepository tocRepository;
  private final EditTocUseCase editTocUseCase;

  @Test
  @DisplayName("ov 값 할당과 rebalance 검사")
  void ovAssignAndRebalanceTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");
    // 책 생성
    BookDto bookDto = bookUtils.createBook(user, "TOC 테스트 도서");
    // toc 만들기
    tocUtils.buildTree(bookDto)
      .folder("f1")
      .folder("f2")
      .section("s3");

    // node들 준비
    Book book = bookPersistencePort.findById(bookDto.getId()).get();
    Toc toc = tocRepository.loadEditableToc(book);
    TocFolder f1 = (TocFolder) toc.getRootFolder().getChildren().get(0);
    TocFolder f2 = (TocFolder) toc.getRootFolder().getChildren().get(1);
    TocSection s3 = (TocSection) toc.getRootFolder().getChildren().get(2);
    assertEquals(TocNodeConfig.OV_START, f1.getOv());
    assertEquals(TocNodeConfig.OV_START + TocNodeConfig.OV_GAP, f2.getOv());
    assertEquals(TocNodeConfig.OV_START + 2 * TocNodeConfig.OV_GAP, s3.getOv());
    // ov 강제 할당
    f2.setOv(100);
    s3.setOv(101);
    // rebalance - 폴더1을 인덱스 1로 이동
    editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        book.getId(),
        f1.getId(),
        toc.getRootFolder().getId(),
        1
      )
    );

    // 엔티티 load 및 ov 재검증
    int afterRebalance_f2Ov = f2.getOv();
    int afterRebalance_f1Ov = f1.getOv();
    int afterRebalance_s3Ov = s3.getOv();

    // OV가 rebalance되었는지 확인
    assertTrue(afterRebalance_f2Ov < afterRebalance_f1Ov);
    assertTrue(afterRebalance_f1Ov < afterRebalance_s3Ov);
    assertEquals(TocNodeConfig.OV_START, afterRebalance_f2Ov);
    assertEquals(TocNodeConfig.OV_START + TocNodeConfig.OV_GAP, afterRebalance_f1Ov);
    assertEquals(TocNodeConfig.OV_START + 2 * TocNodeConfig.OV_GAP, afterRebalance_s3Ov);
  }

  @Test
  @DisplayName("int minimum value에 도달한 경우 rebalance 검사")
  void minimumOvRebalanceTest() {
    // 사용자 생성
    UserDto user = userUtils.createUser("user1");

    // 책 생성
    BookDto bookDto = bookUtils.createBook(user, "최소값 rebalance 테스트");
    UUID bookId = bookDto.getId();
    UUID rootFolderId = editTocUseCase.getToc(user.getUid(), bookId).getRoot().getId();

    // 노드 생성
    FolderDto folder1 = editTocUseCase.createFolder(
      CreateFolderCmd.of(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더1"
      )
    );
    FolderDto folder2 = editTocUseCase.createFolder(
      CreateFolderCmd.of(
        user.getUid(),
        bookId,
        rootFolderId,
        "폴더2"
      )
    );

    Book book = bookPersistencePort.findById(bookId).get();
    Toc toc = tocRepository.loadEditableToc(book);

    // 최소값 근처에 ov 설정
    TocNode en_f1 = toc.get(folder1.getId());
    en_f1.setOv(Integer.MIN_VALUE + 1);

    // folder2를 맨 앞으로 이동시켜 rebalance 유도
    editTocUseCase.relocateNode(
      new RelocateNodeCmd(
        user.getUid(),
        bookId,
        folder2.getId(),
        rootFolderId,
        0
      )
    );

    // rebalance 확인 - 모든 ov가 재조정되었는지 확인
    int newF1Ov = toc.get(folder1.getId()).getOv();
    int newF2Ov = toc.get(folder2.getId()).getOv();

    assertTrue(newF2Ov < newF1Ov); // folder2가 folder1 앞에 있어야 함
    assertTrue(newF2Ov > Integer.MIN_VALUE + 1000); // rebalance 후 최소값에서 충분히 떨어져 있어야 함
  }
}