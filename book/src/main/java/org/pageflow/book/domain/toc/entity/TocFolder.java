package org.pageflow.book.domain.toc.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.NodeTitle;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link InheritanceType#SINGLE_TABLE} 전략이기 때문에, {@link TocSection}과 테이블을 공유함.
 * 때문에 TocFolder만이 가지는 속성에 대해서 nullable = false를 설정하면 TocSection를 생성할 때, 데이터 정합성이 문제가 생겨서 insert가 불가능해진다.
 * 따라서 필드를 추가할 때는 반드시 optional하게 만들 것
 *
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("FOLDER")
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
  @Setter
  @OneToOne(
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL
  )
  @JoinColumn(name = "folder_details_id")
  private FolderDetails folderDetails;

  public TocFolder(
    UUID id,
    Book book,
    String title,
    @Nullable TocFolder parentNode,
    boolean isEditable,
    int ov,
    FolderDetails folderDetails
  ) {
    super(id, book, title, parentNode, isEditable, ov);
    this.folderDetails = folderDetails;
  }

  public static TocFolder createRootFolder(Book book) {
    return new TocFolder(
      UUID.randomUUID(),
      book,
      TocNodeConfig.ROOT_FOLDER_TITLE,
      null,
      true,
      0,
      FolderDetails.create()
    );
  }

  public static TocFolder create(Book book, NodeTitle title, UUID nodeId) {
    return new TocFolder(
      nodeId,
      book,
      title.getValue(),
      null,
      true,
      0,
      FolderDetails.create()
    );
  }

  public void addChild(int index, TocNode child) {
    Preconditions.checkState(child.getParentNodeOrNull() == null);
    Preconditions.checkState(!children.contains(child));

    children.add(index, child);
    child.setParentNode(this);
  }

  public void addChildLast(TocNode child) {
    addChild(children.size(), child);
  }

  public void removeChild(TocNode child) {
    Preconditions.checkState(children.contains(child));
    children.remove(child);
    child.setParentNode(null);
  }

}
