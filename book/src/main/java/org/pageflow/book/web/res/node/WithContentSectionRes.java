package org.pageflow.book.web.res.node;

import lombok.Value;
import org.pageflow.book.application.dto.node.WithContentSectionDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class WithContentSectionRes {
  UUID id;
  String title;
  String content;

  public WithContentSectionRes(WithContentSectionDto dto) {
    this.id = dto.getId();
    this.title = dto.getTitle();
    this.content = dto.getContent().getContent();
  }
}
