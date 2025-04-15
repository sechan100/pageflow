package org.pageflow.book.web.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
public abstract class ReviewForm {

  @Data
  public static class Create {
    @NotBlank
    private String content;

    private int score;
  }

  @Data
  public static class Update {
    @NotBlank
    private String content;

    private int score;
  }
}
