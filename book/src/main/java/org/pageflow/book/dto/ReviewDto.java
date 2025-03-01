package org.pageflow.book.dto;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class ReviewDto {
  UUID id;
  AuthorDto author;
  String content;
  int score;
}
