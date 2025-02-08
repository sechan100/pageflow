package org.pageflow.boundedcontext.auth.domain.token;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.global.property.PropsAware;

import java.time.Duration;
import java.time.Instant;

/**
 * @author : sechan
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  /**
   * epoch milliseconds
   */
  private Long exp;

  /**
   * epoch milliseconds
   */
  private Long iat;



  public RefreshToken(Instant iat, Instant exp) {
    this.iat = iat.toEpochMilli();
    this.exp = exp.toEpochMilli();
  }

  public boolean isExpired() {
    Instant exp = Instant.ofEpochMilli(this.exp);
    return exp.isBefore(Instant.now());
  }

  /**
   * @return RefreshToken
   * @apiNote Session은 반드시 하나의 RefreshToken만을 가져야한다.
   */
  public static RefreshToken issue() {
    Instant now = Instant.now();
    return new RefreshToken(
      now,
      now.plus(Duration.ofDays(PropsAware.use().auth.refreshTokenExpireDays))
    );
  }

}
