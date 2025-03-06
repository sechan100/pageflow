package org.pageflow.user.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.pageflow.common.jpa.TemporaryEntity;
import org.pageflow.common.user.UID;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("email_verification_request")
public class EmailVerificationRequest extends TemporaryEntity<EmailVerificationRequest.EmailCode> {

  @Data
  @AllArgsConstructor
  public static class EmailCode {
    private String email;
    private UUID authCode;
  }

  public static final Long EXPIRED_MILLIS = 1000 * 60 * 10L; // 10분

  private EmailVerificationRequest(String id, EmailCode emailCode, Long expiredAt) {
    super(id, emailCode, expiredAt);
  }

  public static EmailVerificationRequest of(UID uid, String email) {
    UUID authorizationCode = UUID.randomUUID();
    Long expiredAt = System.currentTimeMillis() + EXPIRED_MILLIS;
    return new EmailVerificationRequest(
      generateIdFromUid(uid),
      new EmailCode(email, authorizationCode),
      expiredAt
    );
  }

  public static String generateIdFromUid(UID uid) {
    return EmailVerificationRequest.class.getSimpleName() + "-" + uid;
  }

  @Override
  protected Class<EmailCode> getDataClassType() {
    return EmailCode.class;
  }
}
