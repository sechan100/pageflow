package org.pageflow.book.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
public abstract class SectionForm {

  @Data
  public static class Content {
    @NotBlank
    private String content;
  }
}
