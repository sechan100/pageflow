package org.pageflow.book.domain.toc.constants;

import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;

/**
 * @author : sechan
 */
public enum TocNodeType {
  FOLDER,
  SECTION;

  public static TocNodeType from(TocNode nodeEntity) {
    if(nodeEntity instanceof TocFolder) {
      return FOLDER;
    } else if(nodeEntity instanceof TocSection) {
      return SECTION;
    } else {
      throw new IllegalArgumentException("Unknown node type: " + nodeEntity.getClass());
    }
  }
}