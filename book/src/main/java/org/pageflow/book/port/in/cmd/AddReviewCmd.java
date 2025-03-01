package org.pageflow.book.port.in.cmd;

import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
public class AddReviewCmd {
  private final UID uid;
  private final UUID bookId;
  private final String content;
  private final int score;

  public AddReviewCmd(UID uid, UUID bookId, String content, int score) {
    this.uid = uid;
    this.bookId = bookId;
    this.content = content;
    this.score = score;
  }
}
