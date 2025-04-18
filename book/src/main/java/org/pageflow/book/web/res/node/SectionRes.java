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
  boolean shouldShowTitle;
  boolean shouldBreakSection;

  public static SectionRes from(SectionDto dto) {
    return new SectionRes(
      dto.getId(),
      dto.getTitle(),
      dto.getShouldShowTitle(),
      dto.getShouldBreakSection()
    );
  }

}
