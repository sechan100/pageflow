package org.pageflow.book.domain.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.application.TocNodeType;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.domain.config.TocNodeConfig;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;

import java.util.UUID;


@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
  @OneToOne(
    fetch = FetchType.LAZY,
    optional = true,
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @JoinColumn(name = "content_id", updatable = false)
  private NodeContent content;

  /**
   * readOnly node를 복사하여 editable node를 만드는 경우에 사용하는 복사 팩토리 메서드
   */
  public TocNode(TocNode node, TocNode parentNode) {
    Preconditions.checkArgument(!node.isEditable);
    Preconditions.checkArgument(
      parentNode == null ? true : parentNode.isFolder(),
      "parentNode는 null(rootFolder를 위한 null 참조)거나 Folder여야 합니다."
    );

    this.id = UUID.randomUUID();
    this.book = node.book;
    this.title = node.title;
    this.parentNode = parentNode;
    this.isEditable = true;
    this.ov = node.ov;
    this.type = node.type;
    this.content = node.content != null ? NodeContent.copy(node.content) : null;
  }

  public static TocNode createRootFolder(Book book) {
    return new TocNode(
      UUID.randomUUID(),
      book,
      TocNodeConfig.ROOT_FOLDER_TITLE,
      null,
      true,
      0,
      TocNodeType.FOLDER,
      null
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
      null,
      true,
      0,
      TocNodeType.FOLDER,
      null
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
      null,
      true,
      0,
      TocNodeType.SECTION,
      NodeContent.create(book)
    );
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

  /**
   * @return
   * @throws IllegalStateException this가 {@link TocNodeType#SECTION}이 아닌 경우
   */
  public NodeContent getContent() {
    Preconditions.checkState(isSection());
    return this.content;
  }

  public void setEditable(boolean editable) {
    this.isEditable = editable;
  }

  public boolean isRootFolder() {
    return isFolder() && this.parentNode == null && this.title.equals(TocNodeConfig.ROOT_FOLDER_TITLE);
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
   */
  public TocNode getParentNodeOrNull() {
    return this.parentNode;
  }

  @PreUpdate
  private void preUpdate() {
    boolean isRootFolderTitle = this.title.equals(TocNodeConfig.ROOT_FOLDER_TITLE);
    if(!isRootFolderTitle && this.parentNode == null) {
      throw new IllegalStateException("root folder가 아닌 node는 반드시 부모가 지정되어야 합니다.");
    }
  }

}
