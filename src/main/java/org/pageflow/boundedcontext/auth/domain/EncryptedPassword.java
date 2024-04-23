package org.pageflow.boundedcontext.auth.domain;

import org.pageflow.boundedcontext.auth.application.springsecurity.common.PasswordEncoderConfig;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : sechan
 */
public class EncryptedPassword extends SingleValueWrapper<String> {
    private static final PasswordEncoder ENCODER = PasswordEncoderConfig.PASSWORD_ENCODER;

    private EncryptedPassword(String value) {
        super(value);
    }

    public static EncryptedPassword of(String encryptedPassword) {
        return new EncryptedPassword(encryptedPassword);
    }

    public boolean matches(String rawPassword) {
        return ENCODER.matches(rawPassword, value);
    }

}
