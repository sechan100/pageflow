package org.pageflow.boundedcontext.book.dto;

import lombok.Value;

import java.util.UUID;


/**
 * @author : sechan
 */
public abstract class SectionDto {
  @Value
  public static class WithContent {
    UUID id;
    String title;
    String content;
  }

  @Value
  public static class MetaData {
    UUID id;
    String title;
  }
}
