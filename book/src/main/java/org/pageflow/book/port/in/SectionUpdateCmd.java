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
public class SectionUpdateCmd {
  private final UUID id;
  private final NodeTitle title;
  private final String content;

  public static SectionUpdateCmd of(
    UUID id,
    String title,
    String content
  ) {
    return new SectionUpdateCmd(
      id,
      NodeTitle.of(title),
      content
    );
  }
}
