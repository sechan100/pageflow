package org.pageflow.book.application;

import lombok.Value;
import org.pageflow.book.domain.enums.BookStatus;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class BookStatusRequirement {
  BookStatus currentStatus;
  List<BookStatus> requiredStatuses;


  public static BookStatusRequirement of(BookStatus currentStatus, List<BookStatus> requiredStatuses) {
    return new BookStatusRequirement(currentStatus, requiredStatuses);
  }
}
