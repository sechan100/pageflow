package org.pageflow.book.port.in.cmd;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class RelocateNodeCmd {
  UUID bookId;
  UUID nodeId;
  UUID destFolderId;
  int destIndex;
}
