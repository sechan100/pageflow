package org.pageflow.file.service;

import com.google.common.base.Preconditions;
import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.InvalidField;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
  private final static Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
    "image/jpeg", "image/png", "image/gif", "image/bmp", "image/tiff", "image/webp"
  ));

  private final Tika tika = new Tika();

  @Override
  public boolean accept(FileType fileType) {
    return ACCEPTABLE_FILE_TYPES.contains(fileType);
  }

  /**
   * @param file file이 비어있지 않고, originalFilename이 존재해야한다.
   * @code FIELD_VALIDATION_ERROR: 이미지 파일 검증에 실패한 경우
   * @code INTERNAL_SERVER_ERROR: IO 작업중 에러가 발생한 경우
   */
  @Override
  public Result validateFile(MultipartFile file) {
    // 선조건 ======================
    Preconditions.checkState(!file.isEmpty(), "파일이 비어있습니다.");
    Preconditions.checkState(file.getOriginalFilename() != null, "파일명이 존재하지 않습니다.");

    // 파일 검증 =======================
    FieldValidationResult validation = new FieldValidationResult();
    try {
      // MIME 타입 검증 (Apache Tika)
      String detectedType = tika.detect(file.getInputStream());
      if(!ALLOWED_MIME_TYPES.contains(detectedType)) {
        validation.addInvalidField(InvalidField.builder()
          .field("file")
          .reason(FieldReason.INVALID_FORMAT)
          .message("허용되지 않는 파일 형식입니다: " + detectedType)
          .value(file)
          .build()
        );
      }

      // 이미지 완전성 검증
      BufferedImage image = ImageIO.read(file.getInputStream());
      if(image == null) {
        validation.addInvalidField(
          InvalidField.builder()
            .field("file")
            .reason(FieldReason.INVALID_FORMAT)
            .message("유효한 이미지 파일이 아닙니다.")
            .value(file)
            .build()
        );
      }

      // 파일명 소독
      String originalFilename = file.getOriginalFilename();
      if(originalFilename.contains("..") ||
        originalFilename.contains("/") ||
        originalFilename.contains("\\")
      ) {
        validation.addInvalidField(
          InvalidField.builder()
            .field("file")
            .reason(FieldReason.INVALID_FORMAT)
            .message("파일명에 허용되지 않는 문자가 포함되어 있습니다.")
            .value(file)
            .build()
        );
      }

      // 결과 반환 =================
      if(validation.isValid()) {
        return Result.SUCCESS();
      } else {
        return Result.of(CommonCode.FIELD_VALIDATION_ERROR, validation);
      }
    } catch(IOException e) {
      return Result.of(CommonCode.INTERNAL_SERVER_ERROR);
    }
  }
}
