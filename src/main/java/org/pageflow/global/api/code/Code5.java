package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.global.api.ApiException;

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
@AllArgsConstructor
public enum Code5 implements ApiCode {
    // 5000: 서버 프로그램 에러
      INTERNAL_SERVER_ERROR(5000,"서버 에러; 로그 참고.")

    // 5100: 인프라 시스템 에러
    , FILE_SYSTEM_ERROR(5100, "파일시스템 에러")
    , DATABASE_ERROR(5110, "DB 에러")
    , EMAIL_SERVICE_ERROR(5120, "이메일 서비스 에러")
    , EXTERNAL_API_ERROR(5130, "외부 API 에러")



    // ######################
    ;
    private final int code;
    private final String message;
    public String getFeedback() {
        return "현재 서버가 원활하지 않습니다. 잠시후 다시 시도해주세요.";
    }

    public ApiException fire() {
        return new ApiException(this, this.getFeedback(), null);
    }
}
