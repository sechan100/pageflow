package org.pageflow.file.model;

import org.pageflow.file.shared.FileType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public interface FileUploadCmd {
  MultipartFile getFile();

  String getOwnerId();

  FileType getFileType();
}
