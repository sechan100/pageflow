package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.user.domain.entity.Profile;

import java.util.UUID;


/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "book")
public class Book extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private Profile author;

  @Getter
  @Column(nullable = false)
  private String title;

  @Getter
  @Column(nullable = false)
  private String coverImageUrl;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookStatus status;

  /**
   * publish되거나 revise될 때마다 1씩 증가한다.
   */
  @Getter
  @Column(nullable = false)
  private Integer edition;


  public static Book create(
    UUID id,
    Author author,
    BookTitle title,
    String coverImageUrl
  ) {
    return new Book(
      id,
      author.getProfileJpaEntity(),
      title.getValue(),
      coverImageUrl,
      BookStatus.DRAFT,
      0
    );
  }



  public Author getAuthor() {
    return new Author(author);
  }

  public void changeTitle(BookTitle title) {
    this.title = title.getValue();
  }

  public void changeCoverImageUrl(String url){
    this.coverImageUrl = url;
  }
}
