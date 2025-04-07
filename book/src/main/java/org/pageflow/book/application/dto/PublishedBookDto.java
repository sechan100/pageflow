package org.pageflow.book.application.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Book;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class PublishedBookDto {
  UUID id;
  String title;
  String coverImageUrl;
  String description;
  int edition;
  AuthorDto author;


  public static PublishedBookDto from(Book book) {
    return new PublishedBookDto(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      book.getDescription(),
      book.getEdition(),
      AuthorDto.from(book.getAuthor())
    );
  }
}
