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
public class UpdateSectionCmd {
  private final UUID id;
  private final NodeTitle title;

  public static UpdateSectionCmd createCmd(
    UUID id,
    String title
  ) {
    return new UpdateSectionCmd(
      id,
      NodeTitle.of(title)
    );
  }
}
