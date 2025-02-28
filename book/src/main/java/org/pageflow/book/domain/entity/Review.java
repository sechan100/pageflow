package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.Author;
import org.pageflow.user.domain.entity.Profile;

import java.util.UUID;

/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "review")
public class Review {

  @Id
  @Getter
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private Profile author;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id")
  private Book book;

  @Lob
  @Getter
  // TEXT: 64KB, 한글기준 약 21,845자
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  /**
   * 1 ~ 5
   */
  @Getter
  @Column(nullable = false)
  private int score;


  public Author getAuthor() {
    return new Author(author);
  }
}
