package org.pageflow.book.web.res.node;

import lombok.Value;
import org.pageflow.book.application.dto.node.FolderDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class FolderRes {
  UUID id;
  String title;

  public FolderRes(FolderDto dto) {
    this.id = dto.getId();
    this.title = dto.getTitle();
  }
}
