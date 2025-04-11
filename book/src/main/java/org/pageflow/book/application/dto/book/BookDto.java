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


  public static BookDto from(Book book) {
    return new BookDto(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      book.getDescription(),
      book.getStatus(),
      book.getVisibility(),
      book.getAuthor().getUid()
    );
  }
}
