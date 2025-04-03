package org.pageflow.book.adapter.in.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
public class CreateBookReq {
  @NotBlank
  private String title;
}
