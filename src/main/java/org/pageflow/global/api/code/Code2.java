package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.global.api.ApiException;

/**
 * <p>LEVEL: 2000</p>
 * <p>
 *     메타 요청에 관한 응답을 다룬다.
 *     기본 SUCCESS(2000)은 요청에 대한 처리가 정확하게 성공했을 경우이다.
 *     분기된 요청, 처리중인 요청 또는 아예 잘못된 요청등에서 다룬다.
 * </p>
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum Code2 implements ApiCode {
    // 2000: 성공
      SUCCESS(2000, "성공", "요청이 성공적으로 처리되었습니다.")

    // 2100: 요청이 정상 처리되었지만, 아직 처리중인 경우
    , PROCESSING(2100, "요청 처리중; not fail", "정상 요청되어, 처리중입니다.")

    // 2200: 분기
    , OAUTH2_SIGNUP_REQUIRED(2200, "OAuth2 회원가입이 필요", "로그인하기 위해서, 회원가입을 먼저 진행해주세요!")


    // 2500: 메타 요청
    , INVALID_REQUEST(2500, "올바르지 않은 요청", "올바르지 않은 요청입니다.")

    // 2560: OAuth2 관련
    , INVALID_OAUTH2_STATE(2560, "OAuth2 state가 없거나 올바르지 않음", "올바르지 않은 요청입니다.")




    // ######################
    ;
    private final int code;
    private final String message;
    private final String feedback;

    public ApiException fire() {
        return new ApiException(this, this.getFeedback(), null);
    }
}
