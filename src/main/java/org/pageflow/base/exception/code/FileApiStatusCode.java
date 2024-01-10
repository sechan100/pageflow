package org.pageflow.base.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum FileApiStatusCode implements FeedbackCode {
    
    // 비어있음
      BLANK_FILE("파일이 비어있습니다.")
    , BLANK_FILE_NAME("파일 이름이 없습니다.")
    , BLANK_FILE_PATH("파일 경로가 비어있습니다.")
    
    // 유효하지 않은 값
    , INVALID_FILE_NAME("'{0}'은(는) 유효하지 않은 파일 이름입니다.")
    , INVALID_FILE_EXTENSION("'{0}'에 유효한 파일 확장자가 없습니다.")
    , INVALID_FILE_PATH("유효하지 않은 파일 경로입니다.")
    
    ;
    private final String messageTemplate;
}
