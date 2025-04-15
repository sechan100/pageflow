package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.toc.entity.TocSection;

/**
 * @author : sechan
 */
@Value
public class SectionContentDto {
  String content;

  public static SectionContentDto from(TocSection section) {
    return new SectionContentDto(
      section.getContent().getContent()
    );
  }
}
