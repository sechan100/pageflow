package org.pageflow.book.port.in.cmd;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RelocateNodeCmd {
  private final UID uid;
  private final UUID nodeId;
  private final UUID destFolderId;
  private final int destIndex;
}
