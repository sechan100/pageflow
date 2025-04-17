package org.pageflow.book.domain.toc.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.NodeTitle;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.common.result.Result;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("FOLDER")
@Table(name = "toc_folder")
public class TocFolder extends TocNode {

  @Getter
  @OrderBy("ov ASC")
  @OneToMany(
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL,
    mappedBy = "parentNode"
  )
  private final List<TocNode> children = new ArrayList<>(5);

  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FolderDesign design;

  private TocFolder(
    UUID id,
    Book book,
    String title,
    @Nullable TocFolder parentNode,
    boolean isEditable,
    int ov,
    FolderDesign design
  ) {
    super(id, book, title, parentNode, isEditable, ov);
    this.design = design;
  }

  public static TocFolder createRootFolder(Book book) {
    return new TocFolder(
      UUID.randomUUID(),
      book,
      TocNodeConfig.ROOT_FOLDER_TITLE,
      null,
      true,
      0,
      FolderDesign.DEFAULT
    );
  }

  public static TocFolder create(Book book, NodeTitle title) {
    return new TocFolder(
      UUID.randomUUID(),
      book,
      title.getValue(),
      null,
      true,
      0,
      FolderDesign.DEFAULT
    );
  }

  public static TocFolder copyFromReadOnlyToEditable(TocFolder readOnlyFolder) {
    Preconditions.checkArgument(readOnlyFolder.isReadOnly());
    return new TocFolder(
      UUID.randomUUID(),
      readOnlyFolder.getBook(),
      readOnlyFolder.getTitle(),
      null,
      true,
      readOnlyFolder.getOv(),
      readOnlyFolder.getDesign()
    );
  }

  public void addChild(int index, TocNode child) {
    Preconditions.checkState(this.isEditable());
    Preconditions.checkState(child.getParentNodeOrNull() == null);
    Preconditions.checkState(!children.contains(child));

    children.add(index, child);
    child.setParentNode(this);
  }

  public void addChildLast(TocNode child) {
    addChild(children.size(), child);
  }

  public void removeChild(TocNode child) {
    Preconditions.checkState(this.isEditable());
    Preconditions.checkState(children.contains(child));
    children.remove(child);
    child.setParentNode(null);
  }

  public Result<Void> changeDesign(FolderDesign design) {
    Preconditions.checkState(this.isEditable());
    this.design = design;
  }

}
