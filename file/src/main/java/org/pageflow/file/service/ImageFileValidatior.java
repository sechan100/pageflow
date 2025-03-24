package org.pageflow.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.pageflow.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageFileValidatior {
  private final Tika tika = new Tika();

  public Result validateImageFile(MultipartFile image) {
    return Result.success();
  }
}
