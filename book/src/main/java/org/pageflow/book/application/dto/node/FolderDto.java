package org.pageflow.book.application.dto.node;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.book.domain.entity.TocNode;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class FolderDto {
  UUID id;
  String title;

  public static FolderDto from(TocNode folder) {
    Preconditions.checkState(folder.isParentableNode());
    return new FolderDto(
      folder.getId(),
      folder.getTitle()
    );
  }
}
