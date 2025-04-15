package org.pageflow.book.usecase.cmd;

import lombok.Value;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class CreateFolderCmd {
  UID uid;
  UUID bookId;
  UUID parentNodeId;
  String title;
}
