package org.pageflow.book.adapter.in.res.book;

import lombok.Value;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.enums.BookStatus;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class MyBookRes {
  UUID id;
  String title;
  String coverImageUrl;
  BookStatus status;

  public MyBookRes(BookDto dto) {
    this.id = dto.getId();
    this.title = dto.getTitle();
    this.coverImageUrl = dto.getCoverImageUrl();
    this.status = dto.getStatus();
  }
}
