package org.pageflow.book.port.in.cmd;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.domain.NodeTitle;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateFolderCmd {
  private final UUID id;
  private final NodeTitle title;


  public static UpdateFolderCmd of(UUID id, String title) {
    return new UpdateFolderCmd(
      id,
      NodeTitle.of(title)
    );
  }
}
