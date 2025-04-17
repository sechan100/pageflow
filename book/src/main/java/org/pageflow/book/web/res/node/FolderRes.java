package org.pageflow.book.web.res.node;

import lombok.Value;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.domain.toc.entity.FolderDesign;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class FolderRes {
  UUID id;
  String title;
  FolderDesign design;

  public static FolderRes from(FolderDto dto) {
    return new FolderRes(
      dto.getId(),
      dto.getTitle(),
      FolderDesign.DEFAULT
    );
  }
}
