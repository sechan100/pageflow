package org.pageflow.boundedcontext.common.value;

import lombok.Getter;
import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public class Email extends SingleValueWrapper<String> {
    @Getter
    private final boolean isVerified;

    private Email(String value, boolean isVerified){
        super(value);
        this.isVerified = isVerified;
    }

    public static Email of(String value, boolean isVerified){
        validate(value);
        return new Email(value, isVerified);
    }

    public static Email ofVerified(String value){
        return of(value, true);
    }

    public static Email ofUnverified(String value){
        return of(value, false);
    }

    private static void validate(String email) {
        assert email!=null;
        // 정규식
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw Code4.FORMAT_MISMATCH.feedback(t -> t.getEmail_RegexMismatch());
        }
    }
}
