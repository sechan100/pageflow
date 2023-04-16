package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.constants.TocNodeType;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Getter
public class Toc {
  private final Book book;
  private final TocFolder rootFolder;

  public Toc(Book book, TocFolder rootFolder) {
    Preconditions.checkArgument(rootFolder.isRootFolder());
    this.book = book;
    this.rootFolder = rootFolder;
  }

  public TocNode get(UUID nodeId) {
    return _findNodeRecursive(rootFolder, nodeId);
  }

  public void forEachNode(Consumer<TocNode> consumer) {
    forEachNodeRecursive(rootFolder, consumer);
  }

  public boolean isEditableToc() {
    return rootFolder.isEditable();
  }

  public boolean isReadOnlyToc() {
    return rootFolder.isReadOnly();
  }

  private static TocNode _findNodeRecursive(TocNode node, UUID targetId) {
    if(node.getId().equals(targetId)) {
      return node;
    }
    if(node instanceof TocFolder folder) {
      for(TocNode child : folder.getChildren()) {
        TocNode found = _findNodeRecursive(child, targetId);
        if(found != null) {
          return found;
        }
      }
    }
    return null;
  }

  private static void forEachNodeRecursive(TocNode node, Consumer<TocNode> consumer) {
    consumer.accept(node);
    if(node instanceof TocFolder folder) {
      for(TocNode child : folder.getChildren()) {
        forEachNodeRecursive(child, consumer);
      }
    }
  }
}
