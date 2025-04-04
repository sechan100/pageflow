package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.UUID;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "node_type")
@Table(
  name = "node",
  uniqueConstraints = {
    // 형제 노드끼리의 순서를 결정하기 위해서는 ov값이 형제들 사이에서 모두 unique해야 한다.
    @UniqueConstraint(name = "siblings_ov_unique", columnNames = {"parent_id", "ov"})
  }
)
public abstract class TocNode extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

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
  private Folder parentNode;

  @Getter
  @Column(nullable = false)
  private Integer ov;


  protected TocNode(
    UUID id,
    Book book,
    String title,
    Folder parentNode,
    Integer ov
  ) {
    this.id = id;
    this.book = book;
    this.title = title;
    this.parentNode = parentNode;
    this.ov = ov;
  }

  /**
   * Folder에서 주로 호출한다. 다른 곳에서는 어지간하면 호출하지 말 것.
   * 호출하는 경우, Folder.children과 일관성이 깨지지 않도록 Folder가 그 자체로만 로드되었거나, 아예 메모리로 올리지 않은 상태에서 사용해야한다.
   *
   * @param parentNode
   * @see Folder
   */
  public void _setParentNode(Folder parentNode) {
    Assert.notNull(parentNode, "null을 부모로 설정할 수 없습니다. (root folder만 가능)");
    this.parentNode = parentNode;
  }

  public void setOv(int ov) {
    this.ov = ov;
  }

  @NotNull
  public Folder ensureParentNode() {
    Assert.notNull(this.parentNode, "Root Folder는 부모 노드를 가지지 않습니다.");
    return this.parentNode;
  }

  public boolean isRootFolder() {
    return this.parentNode == null;
  }

  @PreUpdate
  private void preventRootFolderUpdate() {
    if(isRootFolder()) {
      throw new IllegalStateException("Root Folder cannot be updated");
    }
  }

}
