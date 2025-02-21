package org.pageflow.file.model;

import lombok.Value;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.file.shared.FileType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Value
public class FileUploadCmd {
  FileIdentity fileIdentity;
  MultipartFile file;

  public FileUploadCmd(
    String ownerId,
    FileType fileType,
    MultipartFile file
  ) {
    FieldValidator<MultipartFile> validator = new FieldValidator<>("file", file)
      .rule(f -> !f.isEmpty(), FieldReason.EMPTY, "파일이 없습니다.")
      .rule(f -> f.getOriginalFilename() != null, FieldReason.INVALID_FORMAT, "파일 이름이 없습니다.");
    validator.validate().throwIfInvalid();

    this.fileIdentity = new FileIdentity(ownerId, fileType.getOwnerType(), fileType);
    this.file = file;
  }
}
