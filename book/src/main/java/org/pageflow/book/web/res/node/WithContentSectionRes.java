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
  boolean shouldShowTitle;
  boolean shouldBreakSection;
  String content;

  public static WithContentSectionRes from(WithContentSectionDto dto) {
    return new WithContentSectionRes(
      dto.getId(),
      dto.getTitle(),
      dto.getShouldShowTitle(),
      dto.getShouldBreakSection(),
      dto.getContent().getContent()
    );
  }
}
