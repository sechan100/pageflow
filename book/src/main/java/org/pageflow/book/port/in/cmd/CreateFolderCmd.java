package org.pageflow.book.port.in.cmd;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateFolderCmd {
  private final UID uid;
  private final UUID parentNodeId;
  private final String title;

  public static CreateFolderCmd withTitle(UID uid, UUID parentNodeId, String title) {
    return new CreateFolderCmd(uid, parentNodeId, title);
  }

  public static CreateFolderCmd withoutTitle(UID uid, UUID parentNodeId) {
    return new CreateFolderCmd(uid, parentNodeId, "새 폴더");
  }

}
