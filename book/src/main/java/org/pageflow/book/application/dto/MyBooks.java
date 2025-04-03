package org.pageflow.book.application.dto;

import lombok.Value;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class MyBooks {
  AuthorDto author;
  List<BookDto> books;
}
