package org.pageflow.book.adapter.in.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Data
public class CreateReviewReq {
  @NotBlank
  private UID uid;

  @NotBlank
  private UUID bookId;

  @NotBlank
  private String content;

  @Min(1)
  @Max(5)
  private int score;
}
