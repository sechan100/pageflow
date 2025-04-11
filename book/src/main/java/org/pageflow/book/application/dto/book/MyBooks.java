package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.application.dto.AuthorDto;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class MyBooks {
  AuthorDto author;
  List<BookDto> books;
}
