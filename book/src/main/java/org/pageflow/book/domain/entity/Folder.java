package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.book.domain.NodeTitle;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DiscriminatorValue("folder")
@Table(name = "folder")
public class Folder extends TocNode {

  @OrderBy("ov ASC")
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "parentNode")
  private final List<TocNode> children = new ArrayList<>(8);

  private Folder(
    UUID id,
    Book book,
    String title,
    Folder parentNode,
    Integer ov
  ) {
    super(id, book, title, parentNode, ov);
  }

  public static Folder create(
    Book book,
    NodeTitle title,
    Folder parentNode,
    Integer ov
  ) {
    return new Folder(
      UUID.randomUUID(),
      book,
      title.getValue(),
      parentNode,
      ov
    );
  }

  public static Folder createRootFolder(Book book) {
    return new Folder(
      UUID.randomUUID(),
      book,
      ":root",
      null,
      0
    );
  }

  public void changeTitle(NodeTitle title) {
    this.title = title.getValue();
  }

  public List<TocNode> getReadOnlyChildren() {
    return Collections.unmodifiableList(children);
  }

  public int childrenSize() {
    return children.size();
  }

  public void addChild(int index, TocNode child) {
    Assert.isTrue(!children.contains(child), "이미 자식으로 존재하는 노드입니다.");
    children.add(index, child);
    child._setParentNode(this);
  }

  public void addChild(TocNode child) {
    this.addChild(children.size(), child);
  }

  public boolean removeChild(TocNode child) {
    return children.remove(child);
  }

  public boolean hasChild(TocNode child) {
    return children.contains(child);
  }

  public TocNode getChild(int index) {
    return children.get(index);
  }
}
