package org.pageflow.book.application.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class FolderDto {
  UUID id;
  String title;

  public static FolderDto from(TocNode node) {
    if(node instanceof Folder folder) {
      return new FolderDto(
        node.getId(),
        node.getTitle()
      );
    } else {
      throw new IllegalArgumentException("Node is not a folder");
    }
  }
}
