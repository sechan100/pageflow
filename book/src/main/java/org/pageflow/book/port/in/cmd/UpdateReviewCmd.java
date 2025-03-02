package org.pageflow.book.port.in.cmd;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class UpdateReviewCmd {
  UUID reviewId;
  String content;
  int score;

  public static UpdateReviewCmd of(UUID reviewId, String content, int score) {
    return new UpdateReviewCmd(reviewId, content, score);
  }
}
