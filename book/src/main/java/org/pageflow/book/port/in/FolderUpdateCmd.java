package org.pageflow.book.port.in;

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
public class FolderUpdateCmd {
  private final UUID id;
  private final NodeTitle title;


  public static FolderUpdateCmd of(UUID id, String title) {
    return new FolderUpdateCmd(
      id,
      NodeTitle.of(title)
    );
  }
}
