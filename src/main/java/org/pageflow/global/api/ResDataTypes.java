package org.pageflow.global.api;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author : sechan
 */
public abstract class ResDataTypes {

    /**
     * @param name 필드명 (ex. "email")
     */
    public record FieldName(String name){}

    /**
     * @param precondition 선행조건 이름 (ex. "oauth2-signup")
     * @param detail 구체적인 설명 (ex. "oauth2로 로그인하기 위해서 먼저 회원가입이 필요합니다.")
     */
    public record Precondition(String precondition, String detail){}

    /**
     * @param field 필드명 (ex. "email")
     * @param value 필드값 (ex. "noreply@pageflow.org")
     * @param message 사용자 친화 에러메세지
     */
    public record FieldError(String field, @Nullable String value, String message){}
    public record FieldValidation(List<FieldError> errors){}


}
