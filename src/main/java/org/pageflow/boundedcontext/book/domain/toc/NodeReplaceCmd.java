package org.pageflow.boundedcontext.book.domain.toc;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class NodeReplaceCmd {
  UUID bookId;
  UUID nodeId;
  UUID destfolderId;
  int destIndex;
}
