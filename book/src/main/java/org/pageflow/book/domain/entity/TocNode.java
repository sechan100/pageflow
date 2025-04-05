package org.pageflow.book.domain.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.book.domain.config.TocNodeConfig;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;

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
   * {@link Folder#removeChild(TocNode)}에서 사용되는 연관관계 전용 메서드.
   * 절대로 다른 곳에서 호출하지 말 것.
   *
   * @see Folder
   */
  public void __setParentNode(Folder parentNode) {
    this.parentNode = parentNode;
  }

  /**
   * 해당 노드의 부모 노드를 반환한다.
   *
   * @throws IllegalStateException RootFolder인 경우
   */
  public Folder getParentNode() {
    Preconditions.checkState(!this.isRootFolder());
    return this.parentNode;
  }

  public void setOv(int ov) {
    this.ov = ov;
  }

  public boolean isRootFolder() {
    return this.parentNode == null && this.title.equals(TocNodeConfig.ROOT_NODE_TITLE);
  }

  /**
   * root folder가 update되는 것을 방지한다.
   * root folder는 어떤 경우에도 update되면 안된다.
   */
  @PreUpdate
  private void preventRootFolderUpdate() {
    if(isRootFolder()) {
      throw new IllegalStateException("""
        Root Folder(%s)는 업데이트 할 수 없습니다. bookId: %s
        """.formatted(this.id, this.book.getId())
      );
    }
  }

}
