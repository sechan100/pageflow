package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "node_type")
@Table(
  name = "node",
  uniqueConstraints = {
    // 형제 노드끼리의 순서를 결정하기 위해서는 ov값이 형제들 사이에서 모두 unique해야 한다.
    @UniqueConstraint(name = "node_book_id_title_uk", columnNames = {"parent_id", "ov"})
  }
)
public abstract class TocNode extends BaseJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(name = "title", nullable = false)
  private String title;

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

  public void changeParentNode(Folder parentNode){
    Assert.notNull(parentNode, "Node cannot be root folder(parentNode is null)");
    this.parentNode = parentNode;
  }

  public void setOv(int ov){
    this.ov = ov;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isRootFolder(){
    return this.parentNode == null;
  }

  @PreUpdate
  private void preventRootFolderUpdate(){
    if(isRootFolder()){
      throw new IllegalStateException("Root Folder cannot be updated");
    }
  }

}
