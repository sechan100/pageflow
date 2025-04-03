package org.pageflow.book.application;

import org.pageflow.common.utility.SingleValueWrapper;

import java.util.UUID;

/**
 * @author: sechan
 */
public class BookId extends SingleValueWrapper<UUID> {
  public BookId(UUID value) {
    super(value);
  }

  public static BookId from(String string) {
    return new BookId(UUID.fromString(string));
  }
}
