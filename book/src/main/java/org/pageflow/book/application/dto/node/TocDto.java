package org.pageflow.book.application.dto.node;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.pageflow.book.application.TocNodeType;
import org.pageflow.book.domain.entity.TocNode;

import java.util.List;
import java.util.UUID;

@Value
public class TocDto {
  UUID bookId;
  Folder root;

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public abstract static class Node {
    UUID id;
    String title;
    TocNodeType type;

    public Node(TocNode node) {
      this.id = node.getId();
      this.title = node.getTitle();
      this.type = node.getType();
    }
  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Folder extends Node {
    List<Node> children;

    public Folder(TocNode folder, List<Node> children) {
      super(folder);
      Preconditions.checkState(folder.isParentableNode());
      this.children = children;
    }

  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Section extends Node {
    public Section(TocNode section) {
      super(section);
      Preconditions.checkState(section.isSectionType());
    }
  }

}