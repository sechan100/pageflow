package org.pageflow.book.adapter.in.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

/**
 * @author : sechan
 */
@Data
public class CreateFolderReq {
  @NotBlank
  private UUID parentNodeId;

  @NotBlank
  private String title;
}
