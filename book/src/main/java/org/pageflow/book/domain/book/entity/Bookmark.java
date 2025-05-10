package org.pageflow.book.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.user.domain.entity.User;

/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(
  name = "bookmark",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"uid", "book_id"})
  }
)
public class Bookmark extends BaseJpaEntity {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "uid")
  private User user;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id")
  private Book book;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "toc_node_id")
  private TocNode tocNode;

  @Getter
  @Column(name = "section_content_element_id")
  private int sectionContentElementId;


  public static Bookmark create(Author reader, Book book, TocNode tocNode, int sectionContentElementId) {
    return new Bookmark(null, reader.getUserEntity(), book, tocNode, sectionContentElementId);
  }

  public void update(TocNode tocNode, int sectionContentElementId) {
    this.tocNode = tocNode;
    this.sectionContentElementId = sectionContentElementId;
  }


}
