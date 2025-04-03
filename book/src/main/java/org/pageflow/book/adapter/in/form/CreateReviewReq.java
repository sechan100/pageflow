package org.pageflow.book.adapter.in.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
public class CreateReviewReq {
  @NotBlank
  private String content;

  @Min(1)
  @Max(5)
  private int score;
}
