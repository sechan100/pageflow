package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.user.domain.entity.Profile;

import java.util.UUID;


/**
 * @author : sechan
 */
@Entity
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



  @Builder
  public Book(
    UUID id,
    Author author,
    String title,
    String coverImageUrl
  ) {
    this.id = id;
    this.author = author.getProfileJpaEntity();
    this.title = title;
    this.coverImageUrl = coverImageUrl;
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
