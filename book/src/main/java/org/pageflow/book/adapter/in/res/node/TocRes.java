package org.pageflow.book.adapter.in.res.node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.pageflow.book.application.dto.node.TocDto;

import java.util.List;
import java.util.UUID;

@Value
public class TocRes {
  Folder root;

  public static TocRes from(TocDto toc) {
    return new TocRes(
      new Folder(toc.getRoot())
    );
  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static abstract class Node {
    UUID id;
    String title;
    ResponseTocNodeType type;

    public Node(TocDto.Node node) {
      this.id = node.getId();
      this.title = node.getTitle();
      this.type = ResponseTocNodeType.from(node.getType());
    }
  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Folder extends Node {
    List<Node> children;

    public Folder(TocDto.Folder folder) {
      super(folder);
      this.children = folder.getChildren().stream()
        .map(n -> {
          if(n instanceof TocDto.Folder f) {
            return new Folder(f);
          } else if(n instanceof TocDto.Section s) {
            return new Section(s);
          } else {
            throw new IllegalStateException("Unknown node type: " + n.getType());
          }
        })
        .toList();
    }

  }

  @Getter
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Section extends Node {
    public Section(TocDto.Section section) {
      super(section);
    }
  }

}