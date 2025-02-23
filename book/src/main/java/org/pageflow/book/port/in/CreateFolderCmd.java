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
public class CreateFolderCmd {
  private final UUID bookId;
  private final UUID parentNodeId;
  private final NodeTitle title;

  public static CreateFolderCmd withTitle(UUID bookId, UUID parentNodeId, String _title) {
    NodeTitle title = NodeTitle.of(_title);
    return new CreateFolderCmd(bookId, parentNodeId, title);
  }

  public static CreateFolderCmd withoutTitle(UUID bookId, UUID parentNodeId) {
    NodeTitle title = NodeTitle.of("새 폴더");
    return new CreateFolderCmd(bookId, parentNodeId, title);
  }

}
