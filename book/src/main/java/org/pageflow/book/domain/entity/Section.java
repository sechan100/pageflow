package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.domain.SectionHtmlContent;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DiscriminatorValue("section")
@Table(name = "section")
public class Section extends TocNode {

  @Lob
  @Getter
  // MEDIUMTEXT: 16MB, 한글기준 약 4,000,000자
  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String content;

  private Section(
    UUID id,
    Book book,
    String title,
    Folder parentNode,
    String content,
    int ov
  ) {
    super(id, book, title, parentNode, ov);
    this.content = content;
  }

  public static Section create(
    Book book,
    NodeTitle title,
    int ov
  ) {
    return new Section(
      UUID.randomUUID(),
      book,
      title.getValue(),
      null,
      "",
      0
    );
  }

  public void updateContent(SectionHtmlContent content) {
    this.content = content.getContent();
  }

  public void changeTitle(NodeTitle title) {
    this.title = title.getValue();
  }

}
