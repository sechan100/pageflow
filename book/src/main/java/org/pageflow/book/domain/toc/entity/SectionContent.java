package org.pageflow.book.domain.toc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;
import org.pageflow.book.domain.toc.SectionHtmlContent;
import org.pageflow.common.jpa.BaseJpaEntity;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class SectionContent extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @Lob
  @Getter
  // MEDIUMTEXT: 16MB, 한글기준 약 4,000,000자
  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String content;

  @Getter
  @Column(nullable = false)
  private int charCount;


  public static SectionContent create() {
    return new SectionContent(
      UUID.randomUUID(),
      "",
      0
    );
  }

  static SectionContent copy(SectionContent nodeContent) {
    return new SectionContent(
      UUID.randomUUID(),
      nodeContent.getContent(),
      nodeContent.getCharCount()
    );
  }

  public void updateContent(SectionHtmlContent content) {
    this.content = content.getContent();
    this.charCount = content.getCharCount();
  }

}
