package org.pageflow.book.dto;

import lombok.Value;
import org.pageflow.book.domain.entity.Book;
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
  UID authorId;


  public static BookDto from(Book book) {
    return new BookDto(
      book.getId(),
      book.getTitle(),
      book.getCoverImageUrl(),
      book.getAuthor().getUid()
    );
  }
}
