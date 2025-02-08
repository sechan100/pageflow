package org.pageflow.boundedcontext.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;


/**
 * @author : sechan
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("email-verification-request")
public class EmailVarification {
  @Id
  private UUID uid;
  private String email;
  private UUID authCode;

  public EmailVarification(UUID uid, String email, UUID authCode) {
    this.uid = uid;
    this.email = email;
    this.authCode = authCode;
  }
}