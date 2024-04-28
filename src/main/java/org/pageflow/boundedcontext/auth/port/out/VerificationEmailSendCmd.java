package org.pageflow.boundedcontext.auth.port.out;

import lombok.Value;

/**
 * @author : sechan
 */
@Value
public class VerificationEmailSendCmd {
    String emailVerificationId;
    String email;
    String authCode;
}
