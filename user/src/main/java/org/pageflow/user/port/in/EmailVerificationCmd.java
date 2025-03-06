package org.pageflow.user.port.in;

import lombok.Value;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class EmailVerificationCmd {
  UID uid;
  String email;
  UUID authCode;

  public static EmailVerificationCmd of(UUID uid, String email, String authCode){
    return new EmailVerificationCmd(new UID(uid), email, UUID.fromString(authCode));
  }
}
