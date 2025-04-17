package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.usecase.TocFolderUseCase;
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
public class TocFolderTest {
  private final TocFolderUseCase useCase;

  private final UserUtils userUtils;
  private final BookUtils bookUtils;
  private final TocUtils tocUtils;

  @Test
  @DisplayName("폴더 Design 변경 테스트")
  void changeFolderDesignTest() {
    UserDto user = userUtils.createUser("user");
    BookDto book = bookUtils.createBook(user, "book");
    tocUtils.buildTree(book)
      .folder("폴더 1")
      .section("섹션 2");


  }

}
