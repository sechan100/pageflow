package org.pageflow.book.domain.toc;

import org.pageflow.book.domain.entity.TocNode;

import java.util.List;

/**
 * @author : sechan
 */
public class NodeListAscendingValidator {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isAscending(List<TocNode> nodes) {
      for(int i = 0; i < nodes.size() - 1; i++){
        if(!(nodes.get(i).getOv() < nodes.get(i + 1).getOv())){
          return false;
        }
      }
      return true;
    }
}
