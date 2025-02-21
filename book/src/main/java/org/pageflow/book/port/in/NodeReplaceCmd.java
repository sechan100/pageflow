package org.pageflow.book.port.in;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class NodeReplaceCmd {
  UUID bookId;
  UUID nodeId;
  UUID destFolderId;
  int destIndex;
}
