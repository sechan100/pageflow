package org.pageflow.book.port.in.cmd;

import lombok.Getter;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
public class UpdateReviewCmd {
  private final UUID reviewId;
  private final String content;
  private final int score;

  public UpdateReviewCmd(UUID reviewId, String content, int score) {
    this.reviewId = reviewId;
    this.content = content;
    this.score = score;
  }
}
