package org.pageflow.book.domain.toc;

import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;

import java.util.List;

/**
 * @author : sechan
 */
public class NodeListAscendingValidator {

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean isAscending(Folder folder) {
    List<TocNode> nodes = folder.getReadOnlyChildren();
    for(int i = 0; i < nodes.size() - 1; i++) {
      if(!(nodes.get(i).getOv() < nodes.get(i + 1).getOv())) {
        return false;
      }
    }
    return true;
  }
}
