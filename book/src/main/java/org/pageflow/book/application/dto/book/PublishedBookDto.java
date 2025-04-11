package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.application.dto.AuthorDto;
import org.pageflow.book.domain.entity.Book;

import java.util.List;
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
  List<PublishedRecordDto> publishedRecords;
  AuthorDto author;


  public static PublishedBookDto from(Book book) {
    return new PublishedBookDto(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      book.getDescription(),
      book.getPublishedRecords().stream().map(PublishedRecordDto::new).toList(),
      AuthorDto.from(book.getAuthor())
    );
  }
}
