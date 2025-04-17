package org.pageflow.book.application.dto.node;

import lombok.Value;
import org.pageflow.book.domain.toc.entity.SectionContent;

/**
 * @author : sechan
 */
@Value
public class SectionContentDto {
  String content;

  public static SectionContentDto from(SectionContent sectionContent) {
    return new SectionContentDto(
      sectionContent.getContent()
    );
  }
}
