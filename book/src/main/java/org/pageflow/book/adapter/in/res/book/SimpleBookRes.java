package org.pageflow.book.adapter.in.res.book;

import lombok.Value;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.enums.BookStatus;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class SimpleBookRes {
  UUID id;
  String title;
  String coverImageUrl;
  BookStatus status;

  public SimpleBookRes(BookDto book) {
    this.id = book.getId();
    this.title = book.getTitle();
    this.coverImageUrl = book.getCoverImageUrl();
    this.status = book.getStatus();
  }
}
