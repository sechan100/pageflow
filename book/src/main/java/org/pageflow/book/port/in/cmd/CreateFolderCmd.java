package org.pageflow.book.port.in.cmd;

import lombok.Value;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.NodeId;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
public class CreateFolderCmd {
  UID uid;
  BookId bookId;
  NodeId parentNodeId;
  String title;

  public static CreateFolderCmd withTitle(UID uid, BookId bookId, NodeId parentNodeId, String title) {
    return new CreateFolderCmd(uid, bookId, parentNodeId, title);
  }

  public static CreateFolderCmd withoutTitle(UID uid, BookId bookId, NodeId parentNodeId) {
    return new CreateFolderCmd(uid, bookId, parentNodeId, "새 폴더");
  }

}
