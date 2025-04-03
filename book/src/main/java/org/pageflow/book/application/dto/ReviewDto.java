package org.pageflow.book.application.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Review;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class ReviewDto {
  UUID id;
  AuthorDto author;
  String content;
  int score;


  public static ReviewDto from(Review review) {
    return new ReviewDto(
      review.getId(),
      AuthorDto.from(review.getWriter()),
      review.getContent(),
      review.getScore()
    );
  }
}
