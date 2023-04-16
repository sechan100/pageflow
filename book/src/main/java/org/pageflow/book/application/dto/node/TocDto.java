package org.pageflow.book.application.dto.node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.pageflow.book.domain.toc.Toc;
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

  public static TocDto from(Toc toc) {
    return new TocDto(
      toc.getBook().getId(),
      new Folder(toc.getRootFolder())
    );
  }

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

    public Folder(TocFolder folder) {
      super(folder);
      this.children = folder.getChildren().stream()
        .map(child -> {
          if(child instanceof TocFolder childAsFolder) {
            return new Folder(childAsFolder);
          } else if(child instanceof TocSection childAsSection) {
            return new Section(childAsSection);
          } else {
            throw new IllegalStateException("Unknown child type: " + child.getClass());
          }
        })
        .toList();
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