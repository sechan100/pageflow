package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.common.exception.FieldValidationException;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public class CoverImageFile extends SingleValueWrapper<MultipartFile> {

  private CoverImageFile(MultipartFile value) {
    super(value);
  }

  public static CoverImageFile of(MultipartFile value) {
    validate(value);
    return new CoverImageFile(value);
  }

  public static void validate(MultipartFile file) {
    if(file.isEmpty()){
      throw FieldValidationException.builder()
        .message("첨부된 파일이 없습니다.")
        .field("file", null)
        .build();
    }
    if(file.getOriginalFilename()==null){
      throw FieldValidationException.builder()
        .message("파일 이름이 없습니다.")
        .field("filename", null)
        .build();
    }
  }

  @Override
  public String toString() {
    return value.getName();
  }
}
