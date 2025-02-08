package org.pageflow.boundedcontext.book.dto;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class BookDto {

  @Value
  public static class Basic {
    UUID id;
    String title;
    String coverImageUrl;
  }

  @Value
  public static class WithAuthor {
    UUID id;
    String title;
    String coverImageUrl;
    AuthorDto author;
  }


}
