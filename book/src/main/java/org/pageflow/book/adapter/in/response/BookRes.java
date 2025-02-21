package org.pageflow.book.adapter.in.response;

import lombok.Value;
import org.pageflow.book.dto.BookDto;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class BookRes {
  UUID id;
  String title;
  String coverImageUrl;
  UID authorId;


  public static BookRes from(BookDto dto) {
    return new BookRes(
      dto.getId(),
      dto.getTitle(),
      dto.getCoverImageUrl(),
      dto.getAuthorId()
    );
  }
}
