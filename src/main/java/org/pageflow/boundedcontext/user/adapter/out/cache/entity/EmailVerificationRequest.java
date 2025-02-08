package org.pageflow.boundedcontext.user.adapter.out.cache.entity;

import lombok.*;
import org.pageflow.shared.jpa.JpaEntity;
import org.pageflow.shared.type.TSID;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "user-email-verification", timeToLive = 60 * 60)
public class EmailVerificationRequest implements JpaEntity {

  @Id
  private Long uid;

  private String email;

  /**
   * UUID
   */
  private String authorizationCode;

  public EmailVerificationRequest(TSID uid, String email) {
    this.uid = uid.toLong();
    this.email = email;
    this.authorizationCode = UUID.randomUUID().toString();
  }

  public void changeVerificationTargerEmail(String email) {
    this.email = email;
  }


}
