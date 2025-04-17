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
  UUID nodeId;

  public static CreateFolderCmd of(UID uid, UUID bookId, UUID parentNodeId, String title) {
    return new CreateFolderCmd(uid, bookId, parentNodeId, title, UUID.randomUUID());
  }

  public static CreateFolderCmd withId(UID uid, UUID bookId, UUID parentNodeId, String title, UUID nodeId) {
    return new CreateFolderCmd(uid, bookId, parentNodeId, title, nodeId);
  }
}
