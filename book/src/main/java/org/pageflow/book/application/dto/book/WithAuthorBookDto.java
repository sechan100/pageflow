package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.application.dto.author.AuthorDto;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class WithAuthorBookDto {
  UUID id;
  String title;
  String coverImageUrl;
  String description;
  BookStatus status;
  BookVisibility visibility;
  AuthorDto author;

  public static WithAuthorBookDto from(Book book) {
    return new WithAuthorBookDto(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      book.getDescription(),
      book.getStatus(),
      book.getVisibility(),
      AuthorDto.from(book.getAuthor())
    );
  }
}
