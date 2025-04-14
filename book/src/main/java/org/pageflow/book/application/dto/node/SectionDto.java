package org.pageflow.book.application.dto.node;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.book.domain.entity.TocNode;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class SectionDto {
  UUID id;
  String title;

  public static SectionDto from(TocNode section) {
    Preconditions.checkState(section.isSectionType());
    return new SectionDto(
      section.getId(),
      section.getTitle()
    );
  }
}
