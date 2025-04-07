package org.pageflow.book.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.book.domain.SectionHtmlContent;
import org.pageflow.common.jpa.BaseJpaEntity;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class NodeContent extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @Lob
  @Getter
  // MEDIUMTEXT: 16MB, 한글기준 약 4,000,000자
  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String content;

  private NodeContent(
    UUID id,
    String content
  ) {
    this.id = id;
    this.content = content;
  }

  public static NodeContent create() {
    return new NodeContent(
      UUID.randomUUID(),
      ""
    );
  }

  public void updateContent(SectionHtmlContent content) {
    this.content = content.getContent();
  }

}
