package org.pageflow.book.domain.toc;

import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.toc.entity.SectionContent;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.common.result.Ensure;

/**
 * @author : sechan
 */
public class EditableSection {
  private final TocSection section;

  public EditableSection(TocSection section) {
    Ensure.that(section.isEditable(), BookCode.CANNOT_EDIT_BOOK, "해당 섹션은 편집할 수 없습니다.");
    this.section = section;
  }

  public void changeTitle(NodeTitle title) {
    section.setTitle(title.getValue());
  }

  public void updateContent(SectionHtmlContent content) {
    SectionContent sectionContent = section.getSectionDetails().getContent();
    sectionContent.setContent(content.getContent());
    sectionContent.setCharCount(content.getCharCount());
  }

  public void changeShouldShowTitle(boolean shouldShowTitle) {
    section.getSectionDetails().setShouldShowTitle(shouldShowTitle);
  }

  public void changeShouldBreakSection(boolean shouldBreakSection) {
    section.getSectionDetails().setShouldBreakSection(shouldBreakSection);
  }
}
