package org.pageflow.global.exception.business.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum UserCode implements ErrorCode {
      USER_NOT_FOUND("사용자를 찾을 수 없음")
    
    // 유효하지 않음
    , INVALID_USERNAME("유효하지 않은 아이디")
    , INVALID_EMAIL("유효하지 않은 이메일")
    , INVALID_PASSWORD("유효하지 않은 비밀번호")
    , INVALID_PENNAME("유효하지 않은 필명")
    
    // 비밀번호
    , PASSWORD_NOT_MATCH("비밀번호 불일치")
    
    // 중복
    , DUPLICATED_USERNAME("아이디 중복")
    , DUPLICATED_EMAIL("이메일 중복")
    , DUPLICATED_PENNAME("필명 중복")
    
    // 사용불가능한 단어가 포함
    , USERNAME_CONTAINS_FORBIDDEN_WORD("아이디에 사용할 수 없는 단어가 포함됨")
    , PENNAME_CONTAINS_FORBIDDEN_WORD("필명에 사용할 수 없는 단어가 포함됨")
    
    ;
    public final String message;
    
}
