package org.pageflow.book.application.dto.book;

import lombok.Value;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class MyBooksDto {
  List<BookDto> books;
}
