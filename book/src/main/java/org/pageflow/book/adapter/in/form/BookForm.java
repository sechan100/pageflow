package org.pageflow.book.adapter.in.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */

public abstract class BookForm {

  @Data
  public static class Create {
    @NotBlank
    private String title;
  }

  @Data
  public static class Update {
    @NotBlank
    private String title;
  }
  
}
