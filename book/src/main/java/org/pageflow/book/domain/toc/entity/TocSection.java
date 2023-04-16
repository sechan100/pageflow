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
  @JoinColumn(name = "content_id") // SINGLE_TABLE 전략이기 때문에, 'nullable = false'로 하면 안됨
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

  public static TocSection create(Book book, NodeTitle title) {
    return new TocSection(
      UUID.randomUUID(),
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
