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
public class CreateSectionCmd {
  private final UID uid;
  private final UUID parentNodeId;
  private final String title;

  public static CreateSectionCmd withTitle(UID uid, UUID parentNodeId, String title) {
    return new CreateSectionCmd(uid, parentNodeId, title);
  }

  public static CreateSectionCmd withoutTitle(UID uid, UUID parentNodeId) {
    return new CreateSectionCmd(uid, parentNodeId, "새 섹션");
  }
}
