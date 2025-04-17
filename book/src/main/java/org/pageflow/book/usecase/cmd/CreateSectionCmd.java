package org.pageflow.book.usecase.cmd;


import lombok.Value;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class CreateSectionCmd {
  UID uid;
  UUID bookId;
  UUID parentNodeId;
  String title;
  UUID nodeId;

  public static CreateSectionCmd of(UID uid, UUID bookId, UUID parentNodeId, String title) {
    return new CreateSectionCmd(uid, bookId, parentNodeId, title, UUID.randomUUID());
  }
  
  public static CreateSectionCmd withId(UID uid, UUID bookId, UUID parentNodeId, String title, UUID nodeId) {
    return new CreateSectionCmd(uid, bookId, parentNodeId, title, nodeId);
  }
}
