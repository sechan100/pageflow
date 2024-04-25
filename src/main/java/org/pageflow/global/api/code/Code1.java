package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.global.api.FeedbackTemplate;

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




    // ######################
    ;
    private final int code;
    private final String message;
    private final String feedback;

    public ApiException fire() {
        return new ApiException(this, this.getFeedback(), null);
    }
}
