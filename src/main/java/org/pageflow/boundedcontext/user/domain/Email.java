package org.pageflow.boundedcontext.user.domain;

import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public class Email extends SingleValueWrapper<String> {

    private Email(String value){
        super(value);
    }

    public static Email of(String value){
        validate(value);
        return new Email(value);
    }

    private static void validate(String email) {
        if(email == null || email.isEmpty()){
            throw Code4.EMPTY_VALUE.feedback("이메일을 입력해주세요.");
        }
        // 정규식
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw Code4.FORMAT_MISMATCH.feedback(t -> t.getEmail_RegexMismatch());
        }
    }
}
