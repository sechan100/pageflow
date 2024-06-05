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
     * @param field 필드명 (ex. "email")
     * @param value 필드값 (ex. "noreply@pageflow.org")
     * @param message 사용자 친화 에러메세지
     */
    public record FieldError(String field, @Nullable String value, String message){}
    public record FieldValidation(List<FieldError> errors){}


}
