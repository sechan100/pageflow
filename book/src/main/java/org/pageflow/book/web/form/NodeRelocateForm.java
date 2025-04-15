package org.pageflow.book.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

/**
 * @author : sechan
 */
@Data
public class NodeRelocateForm {
  @NotNull
  private UUID targetNodeId;

  @NotNull
  private UUID destFolderId;

  @PositiveOrZero
  private int destIndex;
}
