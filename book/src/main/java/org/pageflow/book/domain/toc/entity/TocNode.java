package org.pageflow.book.domain.toc.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.book.entity.BookStatus;
import org.pageflow.book.domain.toc.NodeTitle;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;

import java.util.UUID;


@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "toc_node_type")
@Table(
  name = "toc_node",
  uniqueConstraints = {
    // 형제 노드끼리의 순서를 결정하기 위해서는 ov값이 형제들 사이에서 모두 unique해야 한다.
    @UniqueConstraint(name = "siblings_ov_unique", columnNames = {"parent_id", "ov"})
  }
)
public abstract class TocNode extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Getter
  @Column(name = "title", nullable = false)
  private String title;

  /**
   * 부모 노드
   * null인 경우 해당 book의 root Folder임을 나타낸다.
   * Root Folder에 대해서는 어떠한 업데이트도 발생해서는 안된다.
   * 생명주기는 소속된 Book과 일치한다.
   */
  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", nullable = true)
  private TocFolder parentNode;

  /**
   * 하나의 책은 최대 두개까지의 TOC를 가질 수 있다.
   * 각각 하나는 읽기전용 TOC, 하나는 편집가능한 TOC이다.
   * 예를 들어, {@link BookStatus#REVISING}인 경우
   * 기존 출판되어있던 toc는 읽기전용 TOC로 남아있고, 이를 기반으로 새로운 toc가 복제되어 편집가능한 toc가 된다.
   */
  @Column(nullable = false)
  private boolean isEditable;

  @Getter
  @Column(nullable = false)
  private int ov;


  public void changeTitle(NodeTitle nodeTitle) {
    Preconditions.checkState(isEditable);
    this.title = nodeTitle.getValue();
  }

  public boolean isRootFolder() {
    return this.title.equals(TocNodeConfig.ROOT_FOLDER_TITLE);
  }

  public boolean isEditable() {
    return this.isEditable;
  }

  public boolean isReadOnly() {
    return !this.isEditable;
  }

  public void setEditable(boolean editable) {
    this.isEditable = editable;
  }

  @Nullable
  public TocFolder getParentNodeOrNull() {
    return this.parentNode;
  }

  public void setOv(int ov) {
    Preconditions.checkState(this.isEditable);
    this.ov = ov;
  }

  // package-private
  void setParentNode(TocFolder folder) {
    Preconditions.checkState(this.isEditable);
    this.parentNode = folder;
  }
}
