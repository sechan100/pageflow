package org.pageflow.book.port.in.cmd;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class ReplaceNodeCmd {
  UUID bookId;
  UUID nodeId;
  UUID destFolderId;
  int destIndex;
}
