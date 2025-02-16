package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DiscriminatorValue("section")
@Table(name = "section")
public class Section extends TocNode {

  @Lob
  @Column(nullable = false)
  private String content;

  public Section(
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

}
