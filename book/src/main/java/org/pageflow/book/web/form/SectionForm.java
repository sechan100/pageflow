package org.pageflow.book.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class SectionForm {

  @Data
  public static class Create {
    @NotNull
    private UUID parentNodeId;

    @NotBlank
    private String title;
  }

  @Data
  public static class Title {
    @NotBlank
    private String title;
  }

  @Data
  public static class Content {
    @NotBlank
    private String content;
  }
}
