package org.pageflow.user.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.pageflow.common.jpa.TemporaryEntity;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("email_verification_request")
public class EmailVerificationRequest extends TemporaryEntity<EmailVerificationRequest.Data> {

  @Value
  public static class Data {
    String email;
    UUID authorizationCode;
  }

  public static final Long EXPIRED_MILLIS = 1000 * 60 * 5L; // 5분

  private EmailVerificationRequest(UUID uid, Data data, Long expiredAt) {
    super(uid.toString(), data, expiredAt);
  }

  public static EmailVerificationRequest of(UUID uid, String email) {
    UUID authorizationCode = UUID.randomUUID();
    Long expiredAt = System.currentTimeMillis() + EXPIRED_MILLIS;
    return new EmailVerificationRequest(
      uid,
      new Data(email, authorizationCode),
      expiredAt
    );
  }

  @Override
  protected Class<Data> getDataClassType() {
    return Data.class;
  }
}
