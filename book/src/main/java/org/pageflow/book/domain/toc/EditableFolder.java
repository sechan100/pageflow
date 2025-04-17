package org.pageflow.book.domain.toc;

import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.toc.entity.FolderDesign;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.common.result.Ensure;

/**
 * 내부적으로 사용되는 folder의 isEditable을 임의로 변경하지 않도록 주의할 것.
 *
 * @author : sechan
 */
public class EditableFolder {
  private final TocFolder folder;

  public EditableFolder(TocFolder folder) {
    Ensure.that(folder.isEditable(), BookCode.CANNOT_EDIT_BOOK, "해당 폴더는 편집할 수 없습니다.");
    this.folder = folder;
  }

  public void changeTitle(NodeTitle title) {
    folder.setTitle(title.getValue());
  }

  public void changeDesign(FolderDesign design) {
    folder.getFolderDetails().setDesign(design);
  }
}
