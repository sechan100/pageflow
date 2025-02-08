package org.pageflow.boundedcontext.auth.in.port;

import org.pageflow.boundedcontext.common.value.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface EmailVerificationUseCase {
  void sendVerificationEmail(UID uid);

  void verify(UID uid, UUID code);

  void unverify(UID uid);
}
