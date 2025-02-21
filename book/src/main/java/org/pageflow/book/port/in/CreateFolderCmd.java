package org.pageflow.book.port.in;

import lombok.Getter;
import org.pageflow.book.domain.NodeTitle;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
public class CreateFolderCmd {
  private final UUID bookId;
  private final UUID parentNodeId;
  private final NodeTitle title;

  public CreateFolderCmd(UUID bookId, UUID parentNodeId, NodeTitle title) {
    this.bookId = bookId;
    this.parentNodeId = parentNodeId;
    this.title = title;
  }

  public static CreateFolderCmd withTitle(UUID bookId, UUID parentNodeId, String _title) {
    NodeTitle title = NodeTitle.validOf(_title);
    return new CreateFolderCmd(bookId, parentNodeId, title);
  }

  public static CreateFolderCmd withoutTitle(UUID bookId, UUID parentNodeId) {
    NodeTitle title = NodeTitle.validOf("새 폴더");
    return new CreateFolderCmd(bookId, parentNodeId, title);
  }

}
