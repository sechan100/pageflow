package org.pageflow.file.service;

import org.pageflow.common.result.Result;
import org.pageflow.file.shared.FileType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public interface FileValidator {
  boolean accept(FileType fileType);

  Result validateFile(MultipartFile file);
}
