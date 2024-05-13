package org.pageflow.boundedcontext.user.domain;

import org.pageflow.boundedcontext.auth.springsecurity.common.PasswordEncoderConfig;
import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : sechan
 */
public class Password extends SingleValueWrapper<String> {
    private static final PasswordEncoder ENCODER = PasswordEncoderConfig.PASSWORD_ENCODER;
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 36;
    private static final String REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])([A-Za-z0-9~!@#$%^&*()+_|=]|-){"+MIN_LENGTH+","+MAX_LENGTH+"}$";
    private static final String REGEX_DESCRIPTION = String.format(
        "비밀번호는 영문, 숫자를 포함한 %d ~ %d 자여야 합니다. 비밀번호 형식을 다시 확인해주세요."
        , Password.MIN_LENGTH, Password.MAX_LENGTH
    );

    private Password(String value){
        super(value);
    }

    public static Password encrypt(String rawPassword) {
        validate(rawPassword);
        String encrypted = ENCODER.encode(rawPassword);
        return new Password(encrypted);
    }

    private static void validate(String password){
        assert password != null;
        // 정규식
        if(!password.matches(REGEX)){
            throw InputValueException.builder()
                .message(REGEX_DESCRIPTION)
                .field("password", password)
                .build();
        }
    }



}
