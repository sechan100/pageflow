package org.pageflow.book.web.res.node;

import lombok.Value;
import org.pageflow.book.application.dto.node.SectionDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class SectionRes {
  UUID id;
  String title;

  public SectionRes(SectionDto dto) {
    this.id = dto.getId();
    this.title = dto.getTitle();
  }
}
