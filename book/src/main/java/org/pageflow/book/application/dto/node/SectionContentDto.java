package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.entity.TocNode;

/**
 * @author : sechan
 */
@Value
public class SectionContentDto {
  String content;

  public static SectionContentDto from(TocNode section) {
    return new SectionContentDto(
      section.getContent().getContent()
    );
  }
}
