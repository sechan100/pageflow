package org.pageflow.book.application;

import lombok.Value;
import org.pageflow.book.domain.enums.BookStatus;

/**
 * @author : sechan
 */
@Value
public class BookStatusData {
  BookStatus currentStatus;
  String message;


  public static BookStatusData of(BookStatus currentStatus, String message) {
    return new BookStatusData(currentStatus, message);
  }
}
