package org.pageflow.book.port.in;


import lombok.Getter;
import org.pageflow.book.domain.NodeTitle;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
public class SectionCreateCmd {
  private final UUID bookId;
  private final UUID parentNodeId;
  private final NodeTitle title;
  private final String content;

  public SectionCreateCmd(UUID bookId, UUID parentNodeId, NodeTitle title) {
    this.bookId = bookId;
    this.parentNodeId = parentNodeId;
    this.title = title;
    this.content = "";
  }

  public static SectionCreateCmd withTitle(UUID bookId, UUID parentNodeId, String _title) {
    NodeTitle title = NodeTitle.of(_title);
    return new SectionCreateCmd(bookId, parentNodeId, title);
  }

  public static SectionCreateCmd withoutTitle(UUID bookId, UUID parentNodeId) {
    NodeTitle title = NodeTitle.of("새 섹션");
    return new SectionCreateCmd(bookId, parentNodeId, title);
  }
}
