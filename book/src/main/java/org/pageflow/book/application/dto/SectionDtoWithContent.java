package org.pageflow.book.application.dto;

import lombok.Value;
import org.pageflow.book.domain.toc.TocSection;

import java.util.UUID;

@Value
public class SectionDtoWithContent {
  UUID id;
  String title;
  String content;

  public static SectionDtoWithContent from(TocSection section) {
    return new SectionDtoWithContent(
      section.getId(),
      section.getTitle(),
      section.getContent().getContent()
    );
  }
}