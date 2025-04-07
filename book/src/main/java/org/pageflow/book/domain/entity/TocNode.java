package org.pageflow.book.domain.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.book.application.TocNodeType;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.domain.config.TocNodeConfig;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;

import java.util.UUID;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(
  name = "node",
  uniqueConstraints = {
    // 형제 노드끼리의 순서를 결정하기 위해서는 ov값이 형제들 사이에서 모두 unique해야 한다.
    @UniqueConstraint(name = "siblings_ov_unique", columnNames = {"parent_id", "ov"})
  }
)
public class TocNode extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Getter
  @Column(name = "title", nullable = false)
  protected String title;

  /**
   * 부모 노드
   * null인 경우 해당 book의 root Folder임을 나타낸다.
   * Root Folder에 대해서는 어떠한 업데이트도 발생해서는 안된다.
   * 생명주기는 소속된 Book과 일치한다.
   */
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "parent_id", nullable = true)
  private TocNode parentNode;

  /**
   * 하나의 책은 최대 두개까지의 TOC를 가질 수 있다.
   * 각각 하나는 읽기전용 TOC, 하나는 편집가능한 TOC이다.
   * 예를 들어, {@link org.pageflow.book.domain.enums.BookStatus#REVISING}인 경우
   * 기존 출판되어있던 toc는 읽기전용 TOC로 남아있고, 이를 기반으로 새로운 toc가 복제되어 편집가능한 toc가 된다.
   */
  @Getter
  @Column(nullable = false)
  private boolean isEditable;

  @Getter
  @Column(nullable = false)
  private int ov;

  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private TocNodeType type;

  /**
   * {@link TocNodeType#SECTION}인 경우에 content를 가진다.
   */
  @Getter
  @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
  @JoinColumn(name = "content_id", updatable = false, nullable = true)
  private NodeContent content;


  private TocNode(
    UUID id,
    Book book,
    String title,
    TocNodeType type
  ) {
    this.id = id;
    this.book = book;
    this.title = title;
    this.parentNode = null;
    this.isEditable = true;
    this.ov = 0;
    this.type = type;
  }

  public static TocNode createRootFolder(Book book) {
    return new TocNode(
      UUID.randomUUID(),
      book,
      TocNodeConfig.EDITABLE_ROOT_NODE_TITLE,
      TocNodeType.FOLDER
    );
  }

  public static TocNode createFolder(
    Book book,
    NodeTitle title
  ) {
    return new TocNode(
      UUID.randomUUID(),
      book,
      title.getValue(),
      TocNodeType.FOLDER
    );
  }

  public static TocNode createSection(
    Book book,
    NodeTitle title
  ) {
    TocNode node = new TocNode(
      UUID.randomUUID(),
      book,
      title.getValue(),
      TocNodeType.SECTION
    );
    node.content = NodeContent.create();
    return node;
  }

  public void changeTitle(NodeTitle title) {
    Preconditions.checkState(isEditable);
    this.title = title.getValue();
  }

  public boolean isSection() {
    return this.getType() == TocNodeType.SECTION;
  }

  public boolean isFolder() {
    return this.getType() == TocNodeType.FOLDER;
  }

  public void setOv(int ov) {
    Preconditions.checkState(isEditable);
    this.ov = ov;
  }

  public boolean isRootFolder() {
    return this.getType() == TocNodeType.FOLDER && this.parentNode == null && this.title.equals(TocNodeConfig.EDITABLE_ROOT_NODE_TITLE);
  }

  public void setParentNode(TocNode parentNode) {
    Preconditions.checkState(parentNode.isFolder());
    Preconditions.checkState(isEditable);
    this.parentNode = parentNode;
  }

  public void makeOrphan() {
    Preconditions.checkState(isEditable);
    this.parentNode = null;
  }

  /**
   * 해당 노드의 부모 노드를 반환한다.
   * 도중에 {@link TocNode#makeOrphan()}을 호출하여 부모를 잠시 null로 만든 경우에 null을 반환한다.
   *
   * @throws IllegalStateException RootFolder인 경우
   */
  public TocNode getParentNodeOrNull() {
    Preconditions.checkState(!this.isRootFolder());
    return this.parentNode;
  }

  @PreUpdate
  private void preUpdate() {
    boolean isRootFolderTitle = this.title.equals(TocNodeConfig.EDITABLE_ROOT_NODE_TITLE);
    if(!isRootFolderTitle && this.parentNode == null) {
      throw new IllegalStateException("root folder가 아닌 node는 반드시 부모가 지정되어야 합니다.");
    }
  }

}
