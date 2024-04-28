package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>LEVEL: 1000</p>
 * <p>
 *     사용자의 인증, 또는 인가와 관련된 에러를 다룬다.
 * </p>
 *
 *
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum Code1 implements ApiCode {
    // 1000: LOGIN
      LOGIN_REQUIRED(1000, "로그인 필요", "로그인이 필요합니다!")

    // 1100: LOGOUT

    // 1200: SESSION
    , SESSION_EXPIRED(1200, "세션(refreshToken) 만료", "세션이 만료되어 다시 로그인이 필요합니다!")
    , SESSION_ID_COOKIE_NOT_FOUND(1420, "세션ID 쿠키 없음", "로그인이 필요합니다!")


    // 1410: ACCESS_TOKEN
    , ACCESS_TOKEN_EXPIRED(1410,"AccessToken 만료", FeedbackTemplate.getINSTANCE().getCanNotFeedback())
    , INVALID_ACCESS_TOKEN(1411,"accessToken jwt 파싱 실패", FeedbackTemplate.getINSTANCE().getCanNotFeedback())

    // 1700: 이메일 인증
    , EMAIL_VERIFICATION_AUTH_CODE_MISMATCH(1700, "이메일 인증 코드 불일치", "이메일 인증코드가 일치하지 않습니다.")
    , APPLIED_EMAIL_VERIFICATION_NOT_FOUND(1701, "요청된 이메일 인증 정보가 없음", "이메일 인증 요청정보가 없습니다.")
    , ALRADY_VERIFIED_EMAIL(1702, "이미 인증된 이메일", "이미 인증된 이메일입니다."),;


    private final int code;
    private final String message;
    private final String feedback;

    public ApiException fire() {
        return new ApiException(this, this.getFeedback(), null, null);
    }

    public ApiException fire(Throwable cause) {
        return new ApiException(this, this.getFeedback(), null, cause);
    }
}
