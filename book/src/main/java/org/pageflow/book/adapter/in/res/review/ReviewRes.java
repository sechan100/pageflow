package org.pageflow.book.adapter.in.res.review;

import lombok.Value;
import org.pageflow.book.adapter.in.res.AuthorRes;
import org.pageflow.book.application.dto.ReviewDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class ReviewRes {
  UUID id;
  AuthorRes writer;
  String content;
  int score;

  public ReviewRes(ReviewDto dto) {
    this.id = dto.getId();
    this.writer = new AuthorRes(dto.getWriter());
    this.content = dto.getContent();
    this.score = dto.getScore();
  }
}
