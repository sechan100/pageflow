package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.TocDto;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.toc.TreeNode;
import org.pageflow.book.port.in.BookSettingsUseCase;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.book.port.out.EditTocPort;
import org.pageflow.book.port.out.ReadTocPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.Result;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.book.utils.TocUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

/**
 * @author : sechan
 */
@PageflowTest
@RequiredArgsConstructor
public class ChangeStatusTocTreeTest {
  private final UserUtils userUtils;
  private final BookUtils bookUtils;
  private final TocUtils tocUtils;
  private final BookSettingsUseCase bookSettingsUseCase;
  private final EditTocUseCase editTocUseCase;

  private final BookPersistencePort bookPersistencePort;
  private final EditTocPort editTocPort;
  private final ReadTocPort readTocPort;

  @Test
  @DisplayName("책 출판시 editableToc가 readonlyToc로 변경되는지 확인")
  void publishTocTreeTest() {
    UserDto user1 = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user1, "출판 테스트 도서");
    tocUtils.buildTree(book)
      .folder("폴더 1", f ->
        f.folder("폴더 1.1")
      )
      .section("섹션 2")
      .section("섹션 3")
      .folder("폴더 4", f ->
        f.section("섹션 4.1")
      );

    TocDto.Toc originalToc = editTocUseCase.getToc(book.getId());

    // 책 출판
    Result publishRes = bookSettingsUseCase.publish(user1.getUid(), book.getId());
    Assertions.assertTrue(publishRes.isSuccess());

    // 출판 후 toc가 readonlyToc로 변경되었는지 확인
    // 나중에 구현

    // 출판 후 개정 시작
    Result reviseRes = bookSettingsUseCase.startRevision(user1.getUid(), book.getId());
    Assertions.assertTrue(reviseRes.isSuccess());

    // 출판 후 editableToc가 복제되었는지 확인
    Book bookEn = bookPersistencePort.findById(book.getId()).get();
    TreeNode rtRoot = readTocPort.loadReadonlyToc(bookEn).buildTree();
    TreeNode etRoot = editTocPort.loadEditableToc(bookEn).buildTree();
    tocUtils.assertSameHierarchyRecusive(rtRoot, etRoot, (readonly, editable) -> {
      // 복제된 id는 같으면 안됨.
      Assertions.assertNotEquals(readonly.getId(), editable.getId());
      Assertions.assertEquals(readonly.getTitle(), editable.getTitle());
      // 복제된 node는 editable, 기존 노드는 readonly
      Assertions.assertFalse(readonly.getIsEditable());
      Assertions.assertTrue(editable.getIsEditable());
    });
  }
}
