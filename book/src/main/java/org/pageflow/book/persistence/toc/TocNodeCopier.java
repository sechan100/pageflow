package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.*;

import java.util.UUID;

/**
 * @author : sechan
 */
public class TocNodeCopier {
  private final boolean isEditable;

  public TocNodeCopier(boolean isEditable) {
    this.isEditable = isEditable;
  }

  public TocFolder copy(TocFolder folder) {
    return new TocFolder(
      UUID.randomUUID(),
      folder.getBook(),
      folder.getTitle(),
      null,
      isEditable,
      folder.getOv(),
      copy(folder.getFolderDetails())
    );
  }

  public TocSection copy(TocSection section) {
    return new TocSection(
      UUID.randomUUID(),
      section.getBook(),
      section.getTitle(),
      null,
      isEditable,
      section.getOv(),
      copy(section.getSectionDetails())
    );
  }

  private FolderDetails copy(FolderDetails folderDetails) {
    return new FolderDetails(
      UUID.randomUUID(),
      folderDetails.getDesign()
    );
  }

  private SectionDetails copy(SectionDetails sectionDetails) {
    return new SectionDetails(
      UUID.randomUUID(),
      copy(sectionDetails.getContent()),
      sectionDetails.getShouldShowTitle(),
      sectionDetails.getShouldBreakSection()
    );
  }

  private SectionContent copy(SectionContent content) {
    return new SectionContent(
      UUID.randomUUID(),
      content.getContent(),
      content.getCharCount()
    );
  }
}
