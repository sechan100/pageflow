package org.pageflow.file.validation;

import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidator;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public class FileValidator {
  private final FieldValidator<MultipartFile> fileValidator;

  public FileValidator(String fieldName, MultipartFile file) {
    FieldValidator<MultipartFile> validator = new FieldValidator<>(fieldName, file)
      .rule(v -> !v.isEmpty(), FieldReason.EMPTY, "첨부된 파일이 없습니다.")
      .rule(v -> v.getOriginalFilename() != null, FieldReason.INVALID_VALUE, "파일 이름이 없습니다.");

    this.fileValidator = validator;
  }

  public void validate() {
    var validation = fileValidator.validate();
    validation.throwIfInvalid();
  }
}
