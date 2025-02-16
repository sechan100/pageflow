package org.pageflow.book.domain.toc;

import lombok.Getter;
import org.pageflow.book.domain.NodeTitle;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
public class FolderCreateCmd {
  private final UUID bookId;
  private final UUID parentNodeId;
  private final NodeTitle title;

  public FolderCreateCmd(UUID bookId, UUID parentNodeId, NodeTitle title) {
    this.bookId = bookId;
    this.parentNodeId = parentNodeId;
    this.title = title;
  }

  public static FolderCreateCmd withTitle(UUID bookId, UUID parentNodeId, String _title) {
    NodeTitle title = NodeTitle.validOf(_title);
    return new FolderCreateCmd(bookId, parentNodeId, title);
  }

  public static FolderCreateCmd withoutTitle(UUID bookId, UUID parentNodeId) {
    NodeTitle title = new NodeTitle("새 폴더");
    return new FolderCreateCmd(bookId, parentNodeId, title);
  }

}
