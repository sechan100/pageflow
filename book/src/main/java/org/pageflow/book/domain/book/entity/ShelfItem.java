package org.pageflow.book.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.book.Author;
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
  name = "shelf_item",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"shelf_owner_id", "book_id"})
  }
)
public class ShelfItem extends BaseJpaEntity {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "shelf_owner_id")
  private User shelfOwner;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id")
  private Book book;

  public static ShelfItem create(Book book, Author shelfOwner) {
    return new ShelfItem(null, shelfOwner.getUserEntity(), book);
  }

  public Book getBook() {
    return book;
  }

}
