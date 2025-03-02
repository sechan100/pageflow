package org.pageflow.book.adapter.in.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateReviewReq {
  @NotBlank
  private String content;

  @Min(1)
  @Max(5)
  private int score;
}