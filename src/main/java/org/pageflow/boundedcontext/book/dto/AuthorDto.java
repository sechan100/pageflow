package org.pageflow.boundedcontext.book.dto;

import lombok.Value;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class AuthorDto {
    UUID id;
  String penname;
  String profileImageUrl;
}
