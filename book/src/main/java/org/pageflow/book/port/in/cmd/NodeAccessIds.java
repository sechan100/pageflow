package org.pageflow.book.port.in.cmd;


import lombok.Value;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.NodeId;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
public class NodeAccessIds {
  UID uid;
  BookId bookId;
  NodeId nodeId;
}
