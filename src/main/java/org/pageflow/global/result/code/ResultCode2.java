package org.pageflow.global.result.code;

import lombok.Getter;
import org.pageflow.boundedcontext.user.adapter.in.web.UserRes;

import java.util.Collection;

/**
 * <p>LEVEL: 2000</p>
 * <p>
 * 메타 요청에 관한 응답을 다룬다.
 * 기본 SUCCESS(2000)은 요청에 대한 처리가 정확하게 성공했을 경우이다.
 * 분기된 요청, 처리중인 요청 또는 아예 잘못된 요청등에서 다룬다.
 * </p>
 *
 * @author : sechan
 */
@Getter
public enum ResultCode2 implements ResultCode {
  // 2000: 성공
  SUCCESS(2000, "성공", Object.class)

  // 2100: 요청이 정상 처리되었지만, 아직 처리중인 경우
  ,
  PROCESSING(2100, "요청 처리중")

  // 2200: 선행조건
  ,
  OAUTH2_SIGNUP_REQUIRED(2201, "OAuth2 회원가입이 필요", UserRes.PreSignuped.class)

  // 2400: http 스펙
  ,
  FAIL_TO_PARSE_HTTP_REQUEST(2400, "http 요청을 해석하지 못했습니다")


  ////////////////////////////////////////////////////////////////////////////
  ;
  private final int code;
  private final String message;
  private final Class<?> dataType;
  private final Class<? extends Collection> collectionType;

  ResultCode2(int code, String message) {
    this(code, message, null, null);
  }

  ResultCode2(int code, String message, Class<?> dataType) {
    this(code, message, dataType, null);
  }

  ResultCode2(int code, String message, Class<?> dataType, Class<? extends Collection> collectionType) {
    this.code = code;
    this.message = message;
    this.dataType = dataType;
    this.collectionType = collectionType;
  }

}
