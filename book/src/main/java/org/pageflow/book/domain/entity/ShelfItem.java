package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.user.domain.entity.Profile;
import org.springframework.data.annotation.Id;

/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "shelf_item")
public class ShelfItem {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private Profile author;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id")
  private Book book;

}
