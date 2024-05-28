package org.pageflow.global.api.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>LEVEL: 5000</p>
 * <p>
 *     서버 에러를 다룬다.
 *     사용자에게 구체적으로 알릴 필요가 없거나, 알려서는 안되는 경우 또는 알릴 수 없는 경우에 사용한다.
 * </p>
 *
 * <p>다음은 대략적인 코드별 유형을 구분한 것이다.</p>
 * <ul>
 *     <li>5000: INTERNAL_SERVER_ERROR</li>
 * </ul>
 *
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public enum ApiCode5 implements ApiCode {
    // 5000: 서버 프로그램 에러
      INTERNAL_SERVER_ERROR(5000,"서버 에러")

    // 5100: 인프라 시스템 에러
    , FILE_SYSTEM_ERROR(5100, "파일시스템 에러")
    , DATABASE_ERROR(5110, "DB 에러")
    , EMAIL_SERVICE_ERROR(5120, "이메일 서비스 에러")
    , EXTERNAL_API_ERROR(5130, "외부 API 에러")



    // ######################
    ;
    private final int code;
    private final String message;
    private final Class<?> dataType;

    ApiCode5(int code, String message) {
        this(code, message, null);
    }
}
