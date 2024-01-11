package org.pageflow.global.business;

import lombok.Getter;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.springframework.http.HttpStatus;

/**
 * @author : sechan
 */
@Getter
public enum UserApiStatusCode implements BizConstraint {
    // 빈 문자열
      BLANK_USERNAME("아이디를 입력해주세요.")
    , BLANK_PASSWORD("비밀번호를 입력해주세요.")
    , BLANK_EMAIL("이메일을 입력해주세요.")
    , BLANK_PENNAME("필명을 입력해주세요.")
    
    // 정규식 불일치
    , USERNAME_REGEX_NOT_MATCH(UserSignupPolicy.USERNAME_REGEX_DISCRIPTION)
    , PASSWORD_REGEX_NOT_MATCH(UserSignupPolicy.PASSWORD_REGEX_DISCRIPTION)
    , EMAIL_REGEX_NOT_MATCH("이메일 형식이 올바르지 않습니다.")
    , PENNAME_REGEX_NOT_MATCH(UserSignupPolicy.PENNAME_REGEX_DISCRIPTION)
    
    // 벤
    , UNUSEABLE_USERNAME("'{0}'는(은) 사용할 수 없는 아이디입니다.")
    , UNUSEABLE_PENNAME("'{0}'는(은)사용할 수 없는 필명입니다.")
    
    // 중복
    , DUPLICATE_USERNAME("'{0}'는(은) 이미 사용중인 아이디입니다.")
    , DUPLICATE_EMAIL("'{0}'는(은) 이미 사용중인 이메일입니다.")
    , DUPLICATE_PENNAME("'{0}'는(은) 이미 사용중인 필명입니다.")
    
    // 불일치
    , PASSWORD_CONFIRM_NOT_MATCH("비밀번호 확인이 일치하지 않습니다.")
    , PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.")
    
    // 찾지 못함
    , USER_NOT_FOUND("사용자를 찾을 수 없습니다.")
    
    // 존재하지 않음
    , USERNAME_NOT_EXIST("'{0}'은(는) 존재하지 않는 아이디입니다.")
    
    // 만료됨
    , SESSION_EXPIRED("로그인 시간이 만료되었습니다. 다시 로그인해주세요.")
    ;
    private final String messageTemplate;
    private final HttpStatus httpStatus;
    
    
    // HttpStatus 400
    UserApiStatusCode(String messageTemplate) {
        this.messageTemplate = messageTemplate;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    // HttpStatus가 400가 아닌 경우
    UserApiStatusCode(String messageTemplate, HttpStatus httpStatus) {
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }
}
