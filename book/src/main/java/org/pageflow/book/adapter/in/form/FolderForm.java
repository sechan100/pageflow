package org.pageflow.book.adapter.in.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class FolderForm {

  @Data
  public static class Create {
    @NotNull
    private UUID parentNodeId;

    @NotBlank
    private String title;
  }

  @Data
  public static class Update {
    @NotBlank
    private String title;
  }
}
