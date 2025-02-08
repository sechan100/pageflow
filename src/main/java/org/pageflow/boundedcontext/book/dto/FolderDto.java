package org.pageflow.boundedcontext.book.dto;

import lombok.Value;

import java.util.UUID;


/**
 * @author : sechan
 */
public abstract class FolderDto {
  @Value
  public static class Basic {
    UUID id;
    String title;
  }
}
