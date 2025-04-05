package org.pageflow.book.domain.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.domain.config.TocNodeConfig;

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
  @OneToMany(
    fetch = FetchType.LAZY,
    cascade = CascadeType.REMOVE,
    mappedBy = "parentNode"
  )
  private final List<TocNode> children = new ArrayList<>(8);


  private Folder(UUID id, Book book, String title, Folder parentNode, Integer ov) {
    super(id, book, title, parentNode, ov);
  }

  public static Folder create(Book book, NodeTitle title, Integer ov) {
    return new Folder(
      UUID.randomUUID(),
      book,
      title.getValue(),
      null,
      ov
    );
  }

  public static Folder createRootFolder(Book book) {
    return new Folder(
      UUID.randomUUID(),
      book,
      TocNodeConfig.ROOT_NODE_TITLE,
      null,
      0
    );
  }

  public void changeTitle(NodeTitle title) {
    this.title = title.getValue();
  }

  // ============================================
  // ========== Children 관련 로직 ================
  // ============================================

  public List<TocNode> getReadOnlyChildren() {
    return Collections.unmodifiableList(children);
  }

  public int childrenSize() {
    return children.size();
  }

  /**
   * 자식 노드를 추가한다.
   *
   * @param index
   * @param child
   * @throws IllegalStateException 해당 folder에 이미 child가 존재하거나, child의 부모노드가 null이 아닌 경우.
   *                               이 경우 {@link Folder#removeChild(TocNode)}를 호출하여 기존 연관관계를 끊어준 후에 사용해야한다.
   */
  public void addChild(int index, TocNode child) {
    Preconditions.checkState(!children.contains(child));

    children.add(index, child);
    child.__setParentNode(this);
  }

  public void addChild(TocNode child) {
    this.addChild(children.size(), child);
  }

  /**
   * child를 제거하고 연관관계를 끊는다.
   *
   * @param child
   * @throws IllegalStateException 해당 folder에 child가 존재하지 않는 경우.
   */
  public void removeChild(TocNode child) {
    boolean removed = children.remove(child);
    if(removed) {
      child.__setParentNode(null);
    } else {
      throw new IllegalStateException("제거할 대상 node가 Folder에 존재하지 않습니다.");
    }
  }

  public boolean hasChild(TocNode child) {
    return children.contains(child);
  }

  public TocNode getChild(int index) {
    return children.get(index);
  }
}
