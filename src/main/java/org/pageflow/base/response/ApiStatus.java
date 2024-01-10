package org.pageflow.base.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 공통 응답 객체의 가장 큰 범주의 상태를 나타낸다.
 * SUCCESS: 요청이 기대한대로 이루어졌음
 * ERROR: 요청 처리중 예외가 발생
 * FEEDBACK: 에러가 실패의 책임이 서버에 있을 때라면, 피드백은 클라이언트에게 책임이 있는 경우이다. 주로 사용자 입력값에 문제가 있는 경우다.
 * 분기: 성공한 것이나 마찬가지지만, 요청이 정확히 기대한대로 이루어진 것이 아니라, 정상적인 로직을 통해서 다른 처리로 분기한 경우이다.
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum ApiStatus {
    // 성공
      SUCCESS("성공")
    // 에러
    , ERROR("에러가 발생했습니다.")
    // 피드백
    , FEEDBACK("사용자 피드백")
    // 분기
    , OAUTH2_SIGNUP_REQUIRED("먼저 oauth2로 회원가입이 필요합니다.");
    
    
    public static final String ATTRIBUTE_KEY = "apiStatus";
    private final String message;
}
