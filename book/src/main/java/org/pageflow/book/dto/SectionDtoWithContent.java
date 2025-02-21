package org.pageflow.book.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Section;

import java.util.UUID;

@Value
public class SectionDtoWithContent {
  UUID id;
  String title;
  String content;

  public static SectionDtoWithContent from(Section section) {
    return new SectionDtoWithContent(
      section.getId(),
      section.getTitle(),
      section.getContent()
    );
  }
}