package org.pageflow.book.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@DiscriminatorValue("folder")
@Table(name = "folder")
public class Folder extends TocNode {

  public Folder(
    UUID id,
    Book book,
    String title,
    Folder parentNode,
    Integer ov
  ) {
    super(id, book, title, parentNode, ov);
  }

}
