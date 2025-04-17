package org.pageflow.test.module.book.usecase;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.toc.entity.FolderDesign;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.TocFolderUseCase;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.book.utils.TocUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@PageflowTest
@RequiredArgsConstructor
public class TocFolderTest {
  private final TocFolderUseCase useCase;
  private final EditTocUseCase editTocUseCase;

  private final UserUtils userUtils;
  private final BookUtils bookUtils;
  private final TocUtils tocUtils;

  @Test
  @DisplayName("폴더 Design 변경 테스트")
  void changeFolderDesignTest() {
    UserDto user = userUtils.createUser("user1");
    BookDto book = bookUtils.createBook(user, "book");

    UUID folderId = UUID.randomUUID();
    UUID sectionId = UUID.randomUUID();
    tocUtils.buildTree(book)
      .folder(folderId)
      .section(sectionId);

    // 폴더 디자인 변경
    NodeIdentifier folderIdenfier = new NodeIdentifier(
      user.getUid(),
      book.getId(),
      folderId
    );
    useCase.changeFolderDesign(folderIdenfier, FolderDesign.SIMPLE);
    FolderDesign changedDesign = editTocUseCase.getFolder(folderIdenfier).getDesign();
    Assertions.assertSame(FolderDesign.SIMPLE, changedDesign);
  }

}
