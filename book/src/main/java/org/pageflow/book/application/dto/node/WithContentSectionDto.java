package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.toc.TocSection;

import java.util.UUID;

@Value
public class WithContentSectionDto {
  UUID id;
  String title;
  String content;

  public static WithContentSectionDto from(TocSection section) {
    return new WithContentSectionDto(
      section.getId(),
      section.getTitle(),
      section.getContent().getContent()
    );
  }
}