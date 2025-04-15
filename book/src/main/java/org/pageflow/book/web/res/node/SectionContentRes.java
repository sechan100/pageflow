package org.pageflow.book.web.res.node;

import lombok.Value;
import org.pageflow.book.application.dto.node.SectionContentDto;

/**
 * @author : sechan
 */
@Value
public class SectionContentRes {
  String content;

  public static SectionContentRes from(SectionContentDto dto) {
    return new SectionContentRes(
      dto.getContent()
    );
  }
}
