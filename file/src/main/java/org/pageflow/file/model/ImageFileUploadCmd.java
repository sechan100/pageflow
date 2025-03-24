package org.pageflow.file.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.file.shared.FileType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public class ImageFileUploadCmd implements FileUploadCmd {
  private final MultipartFile file;
  private final String ownerId;
  private final FileType fileType;

  /**
   * 해당 객체가 생성될 때, file의 값이 존재하고, 로직에 필요한 이름이나 값(확장자)등이 존재하고 올바른 형식인지 검증한다.
   *
   * @code FIELD_VALIDATION_ERROR: file 데이터가 올바르지 않은 경우
   */
  public static Result<ImageFileUploadCmd> createCmd(
    MultipartFile file,
    String ownerId,
    FileType fileType
  ) {
    FieldValidator<MultipartFile> validator = new FieldValidator<>("file", file)
      .rule(f -> !f.isEmpty(), FieldReason.EMPTY, "파일이 없습니다.")
      .rule(f -> f.getOriginalFilename() != null, FieldReason.EMPTY, "파일 이름이 없습니다.");
    FieldValidationResult validation = validator.validate();
    return Result.of(CommonCode.FIELD_VALIDATION_ERROR, validation);
  }
}
