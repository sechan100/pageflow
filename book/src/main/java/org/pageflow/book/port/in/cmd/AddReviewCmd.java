package org.pageflow.book.port.in.cmd;

import lombok.Value;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class AddReviewCmd {
  UID uid;
  UUID bookId;
  String content;
  int score;

  public static AddReviewCmd of(UID uid, UUID bookId, String content, int score) {
    return new AddReviewCmd(uid, bookId, content, score);
  }
}
