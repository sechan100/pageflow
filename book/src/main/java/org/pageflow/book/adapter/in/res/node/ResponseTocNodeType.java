package org.pageflow.book.adapter.in.res.node;

import org.pageflow.book.application.TocNodeType;

/**
 * @author : sechan
 */
public enum ResponseTocNodeType {
  FOLDER, SECTION;


  public static ResponseTocNodeType from(TocNodeType type) {
    return switch(type) {
      case ROOT_FOLDER:
      case FOLDER:
        yield ResponseTocNodeType.FOLDER;
      case SECTION:
        yield ResponseTocNodeType.SECTION;
    };
  }

}
