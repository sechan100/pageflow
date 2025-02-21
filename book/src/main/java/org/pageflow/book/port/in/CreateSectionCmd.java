package org.pageflow.book.port.in;


import lombok.Getter;
import org.pageflow.book.domain.NodeTitle;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
public class CreateSectionCmd {
  private final UUID bookId;
  private final UUID parentNodeId;
  private final NodeTitle title;
  private final String content;

  public CreateSectionCmd(UUID bookId, UUID parentNodeId, NodeTitle title) {
    this.bookId = bookId;
    this.parentNodeId = parentNodeId;
    this.title = title;
    this.content = "";
  }

  public static CreateSectionCmd withTitle(UUID bookId, UUID parentNodeId, String _title) {
    NodeTitle title = NodeTitle.validOf(_title);
    return new CreateSectionCmd(bookId, parentNodeId, title);
  }

  public static CreateSectionCmd withoutTitle(UUID bookId, UUID parentNodeId) {
    NodeTitle title = NodeTitle.validOf("새 섹션");
    return new CreateSectionCmd(bookId, parentNodeId, title);
  }
}
