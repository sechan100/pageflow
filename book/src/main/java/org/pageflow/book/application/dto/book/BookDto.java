package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class BookDto {
  UUID id;
  String title;
  String coverImageUrl;
  String description;
  BookStatus status;
  BookVisibility visibility;
  UID authorId;

  public BookDto(Book book) {
    this.id = book.getId();
    this.title = book.getTitle();
    this.coverImageUrl = book.getCoverImageUrl();
    this.description = book.getDescription();
    this.status = book.getStatus();
    this.visibility = book.getVisibility();
    this.authorId = book.getAuthor().getUid();
  }
}
