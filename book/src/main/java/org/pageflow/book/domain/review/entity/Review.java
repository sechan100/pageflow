package org.pageflow.book.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.review.constants.ReviewScoreConfig;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.InvalidField;
import org.pageflow.user.domain.entity.User;

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
  private User writer;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id")
  private Book book;

  @Getter
  // TEXT: 64KB, 한글기준 약 21,845자
  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  /**
   * 1 ~ 5
   */
  @Getter
  @Column(nullable = false)
  private int score;


  /**
   * @code FIELD_VALIDATION_ERROR: score가 1 ~ 5사이의 정수가 아닌 경우
   */
  public static Result<Review> create(
    Author writer,
    Book book,
    String content,
    int score
  ) {
    Result<Integer> scoreValidationRes = _validateScore(score);
    if(scoreValidationRes.isFailure()) return (Result) scoreValidationRes;

    Review review = new Review(
      UUID.randomUUID(),
      writer.getUserEntity(),
      book,
      content,
      scoreValidationRes.getSuccessData()
    );
    return Result.ok(review);
  }


  public Author getWriter() {
    return new Author(writer);
  }

  public boolean isWriter(UID uid) {
    return writer.getUid().equals(uid);
  }

  public void changeContent(String content) {
    this.content = content;
  }

  /**
   * @code FIELD_VALIDATION_ERROR: score가 1 ~ 5사이의 정수가 아닌 경우
   */
  public Result changeScore(int score) {
    Result<Integer> scoreValidationRes = _validateScore(score);
    if(scoreValidationRes.isFailure()) return scoreValidationRes;
    this.score = scoreValidationRes.getSuccessData();
    return scoreValidationRes;
  }

  /**
   * score가 1 ~ 5사이의 정수인지 확인한다.
   *
   * @code FIELD_VALIDATION_ERROR: score가 1 ~ 5사이의 정수가 아닌 경우
   */
  private static Result<Integer> _validateScore(int score) {
    if(score < ReviewScoreConfig.MIN || score > ReviewScoreConfig.MAX) {
      return Result.unit(CommonCode.FIELD_VALIDATION_ERROR, FieldValidationResult.of(
        InvalidField.builder()
          .field("score")
          .value(score)
          .reason(FieldReason.OUT_OF_RANGE)
          .message(String.format("리뷰 점수는 %d ~ %d 사이의 숫자입니다.", ReviewScoreConfig.MIN, ReviewScoreConfig.MAX))
          .build()
      ));
    }
    return Result.ok(score);
  }
}
