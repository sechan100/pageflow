package org.pageflow.book.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class SectionDto {
  UUID id;
  String title;

  public static SectionDto from(TocNode node) {
    if(node instanceof Section section) {
      return new SectionDto(
        node.getId(),
        node.getTitle()
      );
    } else {
      throw new IllegalArgumentException("Node is not a section");
    }
  }
}
