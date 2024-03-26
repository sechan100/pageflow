package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
import org.pageflow.common.permission.ResourcePermissionContext;
import org.pageflow.test.e2e.PageflowIntegrationTest;
import org.pageflow.test.module.book.utils.BookUtil;
import org.pageflow.test.shared.DataCreator;
import org.pageflow.user.dto.UserDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class TocFeatureTest {
  private final DataCreator dataCreator;
  private final TocUseCase tocUseCase;
  private final ResourcePermissionContext permissionContext;
  private final NodePersistencePort nodePersistencePort;

  @Test
  @DisplayName("ov 값 할당과 rebalance 검사")
  void ovAssignAndRebalanceTest() {
    UserDto user1 = dataCreator.createUser("user1");
    BookDto book = dataCreator.createBook(user1, "책 1");
    UUID bookId = book.getId();
    BookPermission permission = BookUtil.fullPermission(book.getId());
    permissionContext.addResourcePermission(permission);

    // toc 생성
    TocDto.Toc toc = tocUseCase.getToc(book.getId());
    TocDto.Folder root = toc.getRoot();
    UUID rootFolderId = root.getId();
    FolderDto f1 = createFolder(bookId, rootFolderId);
    FolderDto f2 = createFolder(bookId, rootFolderId);
    SectionDtoWithContent s3 = createSection(bookId, rootFolderId);

    // 엔티티 load 및 ov 검증
    TocNode en_f1 = nodePersistencePort.findById(f1.getId()).get();
    Assertions.assertEquals(0, en_f1.getOv());
    TocNode en_f2 = nodePersistencePort.findById(f2.getId()).get();
    TocNode en_s3 = nodePersistencePort.findById(s3.getId()).get();
    Assertions.assertEquals(en_f2.getOv() << 1, en_s3.getOv());

    // ov 강제 할당
    en_f2.setOv(100);
    en_s3.setOv(101);
    nodePersistencePort.flush();
    Assertions.assertEquals(
      100,
      nodePersistencePort.findById(f2.getId()).get().getOv()
    );

    // rebalance
    tocUseCase.relocateNode(
      bookId,
      RelocateNodeCmd.of(
        bookId,
        f1.getId(),
        rootFolderId,
        1
      )
    );

    // 엔티티 load 및 ov 검증
    int afterRebalance_f2Ov = nodePersistencePort.findById(f2.getId()).get().getOv();
    Assertions.assertEquals(0, afterRebalance_f2Ov);
    int afterRebalance_f1Ov = nodePersistencePort.findById(f1.getId()).get().getOv();
    int afterRebalance_s3Ov = nodePersistencePort.findById(s3.getId()).get().getOv();
    Assertions.assertEquals(afterRebalance_f1Ov << 1, afterRebalance_s3Ov);
  }

  @Test
  @DisplayName("int minimun value에 도달한 경우 rebalance 검사")
  void minimumOvRebalanceTest() {
    UserDto user1 = dataCreator.createUser("user1");
    BookDto book = dataCreator.createBook(user1, "책 1");
    UUID bookId = book.getId();
    BookPermission permission = BookUtil.fullPermission(book.getId());
    permissionContext.addResourcePermission(permission);

    // toc 생성
    TocDto.Toc toc = tocUseCase.getToc(book.getId());
    TocDto.Folder root = toc.getRoot();
    UUID rootFolderId = root.getId();
    FolderDto f1 = createFolder(bookId, rootFolderId);
    FolderDto f2 = createFolder(bookId, rootFolderId);
    SectionDtoWithContent s3 = createSection(bookId, rootFolderId);

    // 엔티티 load 및 ov 검증
    TocNode en_f1 = nodePersistencePort.findById(f1.getId()).get();
    Assertions.assertEquals(0, en_f1.getOv());
    TocNode en_f2 = nodePersistencePort.findById(f2.getId()).get();
    TocNode en_s3 = nodePersistencePort.findById(s3.getId()).get();
    Assertions.assertEquals(en_f2.getOv() << 1, en_s3.getOv());

    // ov 강제 할당(min 근처 값)
    en_f1.setOv(Integer.MIN_VALUE + 1);
    nodePersistencePort.flush();
    Assertions.assertEquals(
      Integer.MIN_VALUE + 1,
      nodePersistencePort.findById(en_f1.getId()).get().getOv()
    );

    // rebalance
    tocUseCase.relocateNode(
      bookId,
      RelocateNodeCmd.of(
        bookId,
        f2.getId(),
        rootFolderId,
        0
      )
    );

    // 엔티티 load 및 ov 검증
    int afterRebalance_f2Ov = nodePersistencePort.findById(f2.getId()).get().getOv();
    Assertions.assertEquals(0, afterRebalance_f2Ov);
    int afterRebalance_f1Ov = nodePersistencePort.findById(f1.getId()).get().getOv();
    int afterRebalance_s3Ov = nodePersistencePort.findById(s3.getId()).get().getOv();
    Assertions.assertEquals(afterRebalance_f1Ov << 1, afterRebalance_s3Ov);
  }

  @Test
  @DisplayName("int maximun value에 도달한 경우 rebalance 검사")
  void maximunOvRebalanceTest() {
    UserDto user1 = dataCreator.createUser("user1");
    BookDto book = dataCreator.createBook(user1, "책 1");
    UUID bookId = book.getId();
    BookPermission permission = BookUtil.fullPermission(book.getId());
    permissionContext.addResourcePermission(permission);

    // toc 생성
    TocDto.Toc toc = tocUseCase.getToc(book.getId());
    TocDto.Folder root = toc.getRoot();
    UUID rootFolderId = root.getId();
    FolderDto f1 = createFolder(bookId, rootFolderId);
    FolderDto f2 = createFolder(bookId, rootFolderId);
    SectionDtoWithContent s3 = createSection(bookId, rootFolderId);

    // 엔티티 load 및 ov 검증
    TocNode en_f1 = nodePersistencePort.findById(f1.getId()).get();
    Assertions.assertEquals(0, en_f1.getOv());
    TocNode en_f2 = nodePersistencePort.findById(f2.getId()).get();
    TocNode en_s3 = nodePersistencePort.findById(s3.getId()).get();
    Assertions.assertEquals(en_f2.getOv() << 1, en_s3.getOv());

    // ov 강제 할당(min 근처 값)
    en_s3.setOv(Integer.MAX_VALUE - 1);
    nodePersistencePort.flush();
    Assertions.assertEquals(
      Integer.MAX_VALUE - 1,
      nodePersistencePort.findById(en_s3.getId()).get().getOv()
    );

    // rebalance
    tocUseCase.relocateNode(
      bookId,
      RelocateNodeCmd.of(
        bookId,
        f2.getId(),
        rootFolderId,
        2
      )
    );

    // 엔티티 load 및 ov 검증
    int afterRebalance_f1Ov = nodePersistencePort.findById(f1.getId()).get().getOv();
    Assertions.assertEquals(0, afterRebalance_f1Ov);
    int afterRebalance_s3Ov = nodePersistencePort.findById(s3.getId()).get().getOv();
    int afterRebalance_f2Ov = nodePersistencePort.findById(f2.getId()).get().getOv();
    Assertions.assertEquals(afterRebalance_s3Ov << 1, afterRebalance_f2Ov);
  }


  private FolderDto createFolder(UUID bookId, UUID parentNodeId) {
    return tocUseCase.createFolder(bookId, CreateFolderCmd.withoutTitle(
      bookId,
      parentNodeId
    ));
  }

  private SectionDtoWithContent createSection(UUID bookId, UUID parentNodeId) {
    return tocUseCase.createSection(bookId, CreateSectionCmd.withoutTitle(
      bookId,
      parentNodeId
    ));
  }


}
