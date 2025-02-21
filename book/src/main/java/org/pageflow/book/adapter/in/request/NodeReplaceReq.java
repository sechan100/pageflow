package org.pageflow.book.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

/**
 * @author : sechan
 */
@Data
public class NodeReplaceReq {
  @NotBlank
  private UUID targetNodeId;

  @NotBlank
  private UUID destFolderId;

  @PositiveOrZero
  private int destIndex;
}
