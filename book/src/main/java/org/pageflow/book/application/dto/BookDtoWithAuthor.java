package org.pageflow.book.application.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class BookDtoWithAuthor {
  UUID id;
  String title;
  String coverImageUrl;
  BookStatus status;
  int edition;
  BookVisibility visibility;
  AuthorDto author;

  public static BookDtoWithAuthor from(Book book) {
    return new BookDtoWithAuthor(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      book.getStatus(),
      book.getEdition(),
      book.getVisibility(),
      AuthorDto.from(book.getAuthor())
    );
  }
}
