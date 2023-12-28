package org.pageflow.base.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum ApiStatus {
    
      SUCCESS("성공")
    , ERROR("에러")
    , OAUTH2_SIGNUP_REQUIRED("oauth2로 회원가입이 필요합니다.");
    
    
    public static final String ATTRIBUTE_KEY = "apiStatus";
    private final String message;
}
