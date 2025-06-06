package org.pageflow.book.application.dto;

import lombok.Value;
import org.pageflow.book.application.dto.author.AuthorDto;
import org.pageflow.book.domain.review.entity.Review;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class ReviewDto {
  UUID id;
  AuthorDto writer;
  String content;
  int score;


  public static ReviewDto from(Review review) {
    return new ReviewDto(
      review.getId(),
      AuthorDto.from(review.getUser()),
      review.getContent(),
      review.getScore()
    );
  }
}
