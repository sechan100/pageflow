package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.toc.entity.TocSection;

import java.util.UUID;

@Value
public class WithContentSectionDto {
  UUID id;
  String title;
  boolean shouldShowTitle;
  boolean shouldBreakSection;
  SectionContentDto content;

  public static WithContentSectionDto from(TocSection section) {
    return new WithContentSectionDto(
      section.getId(),
      section.getTitle(),
      section.getSectionDetails().getShouldShowTitle(),
      section.getSectionDetails().getShouldBreakSection(),
      SectionContentDto.from(section.getSectionDetails().getContent())
    );
  }
}