package org.pageflow.book.application.dto.node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.pageflow.book.domain.toc.constants.TocNodeType;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;

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
      this.type = TocNodeType.from(node);
    }
  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Folder extends Node {
    List<Node> children;

    public Folder(TocFolder folder, List<TocDto.Node> children) {
      super(folder);
      this.children = children;
    }
  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Section extends Node {
    public Section(TocSection section) {
      super(section);
    }
  }

}