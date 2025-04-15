package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.toc.entity.TocFolder;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class FolderDto {
  UUID id;
  String title;

  public static FolderDto from(TocFolder folder) {
    return new FolderDto(
      folder.getId(),
      folder.getTitle()
    );
  }
}
