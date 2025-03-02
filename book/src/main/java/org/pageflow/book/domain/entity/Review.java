package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.Author;
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
@Table(name = "review")
public class Review extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "writer_id")
  private Profile writer;

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


  public static Review create(
    Author writer,
    Book book,
    String content,
    int score
  ) {
    return new Review(
      UUID.randomUUID(),
      writer.getProfileJpaEntity(),
      book,
      content,
      _validateScore(score)
    );
  }


  public Author getWriter() {
    return new Author(writer);
  }

  public void changeContent(String content) {
    this.content = content;
  }

  public void changeScore(int score) {
    this.score = _validateScore(score);
  }

  /**
   * score가 1 ~ 5사이의 정수인지 확인한다.
   *
   * @param score
   */
  private static int _validateScore(int score) {
    if(score < 1 || score > 5) {
      throw new IllegalArgumentException("score는 1 ~ 5 사이의 정수입니다. score:" + score);
    }
    return score;
  }
}
