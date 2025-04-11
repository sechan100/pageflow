package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import org.pageflow.book.domain.entity.NodeContent;
import org.pageflow.book.domain.entity.TocNode;

import java.util.UUID;

/**
 * @author : sechan
 */
public class TocSection {
  private final TocNode section;

  public TocSection(TocNode section) {
    Preconditions.checkState(section.isSection());
    this.section = section;
  }

  public UUID getId() {
    return section.getId();
  }

  public String getTitle() {
    return section.getTitle();
  }

  public NodeContent getContent() {
    return section.getContent();
  }

  public TocNode getTocNodeEntity() {
    return section;
  }
}
