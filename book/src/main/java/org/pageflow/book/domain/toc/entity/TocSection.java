package org.pageflow.book.domain.toc.entity;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "toc_section")
public class TocSection extends TocNode {

  @Getter
  @OneToOne(
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL
  )
  @JoinColumn(name = "content_id")
  private SectionContent content;

  private TocSection(
    UUID id,
    Book book,
    String title,
    @Nullable TocFolder parentNode,
    boolean isEditable,
    int ov,
    SectionContent content
  ) {
    super(id, book, title, parentNode, isEditable, ov);
    this.content = content;
  }

  public static TocSection create(Book book, NodeTitle title, UUID nodeId) {
    return new TocSection(
      nodeId,
      book,
      title.getValue(),
      null,
      true,
      0,
      SectionContent.create()
    );
  }

  public static TocSection copyFromReadOnlyToEditable(TocSection readOnlySection) {
    Preconditions.checkArgument(readOnlySection.isReadOnly());

    return new TocSection(
      UUID.randomUUID(),
      readOnlySection.getBook(),
      readOnlySection.getTitle(),
      null,
      true,
      readOnlySection.getOv(),
      SectionContent.copy(readOnlySection.getContent())
    );
  }

}
