package org.pageflow.book.port.in.cmd;


import lombok.Value;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.NodeId;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
public class CreateSectionCmd {
  UID uid;
  BookId bookId;
  NodeId parentNodeId;
  String title;

  public static CreateSectionCmd withTitle(UID uid, BookId bookId, NodeId parentNodeId, String title) {
    return new CreateSectionCmd(uid, bookId, parentNodeId, title);
  }

  public static CreateSectionCmd withoutTitle(UID uid, BookId bookId, NodeId parentNodeId) {
    return new CreateSectionCmd(uid, bookId, parentNodeId, "새 섹션");
  }
}
