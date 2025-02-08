package org.pageflow.boundedcontext.file.model;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.boundedcontext.file.shared.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class FileUploadCmd {
  FileIdentity fileIdentity;
  MultipartFile file;

  public FileUploadCmd(
    UUID ownerId,
    FileType fileType,
    MultipartFile file
  ) {
    assert file!=null;
    Preconditions.checkState(
      !file.isEmpty(),
      "업로드할 파일이 비어있습니다."
    );
    Preconditions.checkNotNull(
      file.getOriginalFilename(),
      "파일 이름이 없습니다."
    );
    this.fileIdentity = new FileIdentity(ownerId, fileType.getOwnerType(), fileType);
    this.file = file;
  }
}
