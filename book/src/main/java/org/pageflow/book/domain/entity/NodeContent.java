package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.SectionHtmlContent;
import org.pageflow.common.jpa.BaseJpaEntity;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class NodeContent extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Lob
  @Getter
  // MEDIUMTEXT: 16MB, 한글기준 약 4,000,000자
  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String content;

  @Getter
  @Column(nullable = false)
  private int charCount;

  public static NodeContent create(Book book) {
    return new NodeContent(
      UUID.randomUUID(),
      book,
      "",
      0
    );
  }

  public static NodeContent copy(NodeContent nodeContent) {
    return new NodeContent(
      UUID.randomUUID(),
      nodeContent.getBook(),
      nodeContent.getContent(),
      nodeContent.getCharCount()
    );
  }

  public void updateContent(SectionHtmlContent content) {
    this.content = content.getContent();
    this.charCount = content.getCharCount();
  }

}
