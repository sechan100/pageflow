package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.domain.book.entity.Bookmark;
import org.pageflow.book.domain.toc.constants.TocNodeType;
import org.pageflow.book.domain.toc.entity.TocNode;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class BookmarkDto {
  UUID bookId;
  UUID tocNodeId;
  TocNodeType tocNodeType;
  int sectionContentElementId;

  public BookmarkDto(Bookmark bookmark) {
    this.bookId = bookmark.getBook().getId();
    TocNode tocNode = bookmark.getTocNode();
    this.tocNodeId = tocNode.getId();
    this.tocNodeType = TocNodeType.from(tocNode);
    this.sectionContentElementId = bookmark.getSectionContentElementId();
  }
}
