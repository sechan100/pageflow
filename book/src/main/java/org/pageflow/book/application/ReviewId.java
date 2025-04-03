package org.pageflow.book.application;

import org.pageflow.common.utility.SingleValueWrapper;

import java.util.UUID;

/**
 * @author : sechan
 */
public class ReviewId extends SingleValueWrapper<UUID> {
  public ReviewId(UUID value) {
    super(value);
  }

  public static ReviewId from(String string) {
    return new ReviewId(UUID.fromString(string));
  }
}
