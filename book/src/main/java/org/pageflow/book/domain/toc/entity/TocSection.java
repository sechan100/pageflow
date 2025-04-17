package org.pageflow.book.domain.toc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.NodeTitle;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * {@link InheritanceType#SINGLE_TABLE} 전략이기 때문에, {@link TocFolder}와 테이블을 공유함.
 * 때문에 TocSection만이 가지는 속성에 대해서 nullable = false를 설정하면 TocFolder를 생성할 때, 데이터 정합성이 문제가 생겨서 insert가 불가능해진다.
 * 따라서 필드를 추가할 때는 반드시 optional하게 만들 것
 *
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("SECTION")
public class TocSection extends TocNode {

  @Getter
  @Setter
  @OneToOne(
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL
  )
  private SectionDetails sectionDetails;

  public TocSection(
    UUID id,
    Book book,
    String title,
    @Nullable TocFolder parentNode,
    boolean isEditable,
    int ov,
    SectionDetails sectionDetails
  ) {
    super(id, book, title, parentNode, isEditable, ov);
    this.sectionDetails = sectionDetails;
  }

  public static TocSection create(Book book, NodeTitle title, UUID nodeId) {
    return new TocSection(
      nodeId,
      book,
      title.getValue(),
      null,
      true,
      0,
      SectionDetails.create()
    );
  }

}
