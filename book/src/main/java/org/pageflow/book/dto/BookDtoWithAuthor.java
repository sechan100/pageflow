package org.pageflow.book.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Book;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class BookDtoWithAuthor {
  UUID id;
  String title;
  String coverImageUrl;
  AuthorDto author;

  public static BookDtoWithAuthor from(Book book) {
    return new BookDtoWithAuthor(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      AuthorDto.from(book.getAuthor())
    );
  }
}
