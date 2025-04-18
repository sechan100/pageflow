package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.toc.entity.TocSection;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class SectionDto {
  UUID id;
  String title;
  boolean shouldShowTitle;
  boolean shouldBreakSection;

  public static SectionDto from(TocSection section) {
    return new SectionDto(
      section.getId(),
      section.getTitle(),
      section.getSectionDetails().getShouldShowTitle(),
      section.getSectionDetails().getShouldBreakSection()
    );
  }
}
