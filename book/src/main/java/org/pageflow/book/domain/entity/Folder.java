package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DiscriminatorValue("folder")
@Table(name = "folder")
public class Folder extends TocNode {

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parentNode")
  private final List<TocNode> children = new ArrayList<>(7);

  public Folder(
    UUID id,
    Book book,
    String title,
    Folder parentNode,
    Integer ov
  ) {
    super(id, book, title, parentNode, ov);
  }

  public static Folder createRootFolder(Book book) {
    return new Folder(
      UUID.randomUUID(),
      book,
      ":root",
      null,
      0
    );
  }

  public void changeTitle(String title) {
    this.title = title;
  }
}
