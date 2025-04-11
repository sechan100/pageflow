package org.pageflow.book.application.dto.author;

import lombok.Value;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.common.user.UID;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class AuthorProfileDto {
  UID id;
  String penname;
  String profileImageUrl;
  List<BookDto> books;
  String bio;
}
