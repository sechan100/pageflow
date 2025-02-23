package org.pageflow.book.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

/**
 * @author : sechan
 */
@Data
public class CreateFolderCmd {
  @NotBlank
  private UUID parentNodeId;

  @NotBlank
  private String title;
}
