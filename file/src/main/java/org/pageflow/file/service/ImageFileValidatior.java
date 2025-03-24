package org.pageflow.file.service;

import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.pageflow.common.result.Result;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageFileValidatior implements FileValidator {
  private static final Array<FileType> ACCEPTABLE_FILE_TYPES = Array.of(
    FileType.USER_PROFILE_IMAGE,
    FileType.BOOK_COVER_IMAGE
  );

  private final Tika tika = new Tika();

  @Override
  public boolean accept(FileType fileType) {
    return ACCEPTABLE_FILE_TYPES.contains(fileType);
  }

  @Override
  public Result validateFile(MultipartFile file) {
    return null;
  }
}
