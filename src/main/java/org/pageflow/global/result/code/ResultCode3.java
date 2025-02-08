package org.pageflow.global.result.code;

import lombok.Getter;

import java.util.Collection;

/**
 * <p>LEVEL: 3000</p>
 * <p>
 * 요청된 리소스에 문제가 있는 경우
 * </p>
 *
 * @author : sechan
 */
@Getter
public enum ResultCode3 implements ResultCode {
  // 3000: 데이터베이스
  DATA_NOT_FOUND(3000, "요청된 데이터를 찾을 수 없음")

  // 3400: 파일
  ,
  FILE_NOT_FOUND(3400, "요청된 파일을 찾을 수 없음")


  ////////////////////////////////////////////////////////////////////////////
  ;
  private final int code;
  private final String message;
  private final Class<?> dataType;
  private final Class<? extends Collection> collectionType;

  ResultCode3(int code, String message) {
    this(code, message, null, null);
  }

  ResultCode3(int code, String message, Class<?> dataType) {
    this(code, message, dataType, null);
  }

  ResultCode3(int code, String message, Class<?> dataType, Class<? extends Collection> collectionType) {
    this.code = code;
    this.message = message;
    this.dataType = dataType;
    this.collectionType = collectionType;
  }

}
