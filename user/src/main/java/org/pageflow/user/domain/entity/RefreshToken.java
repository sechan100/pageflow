package org.pageflow.user.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.common.property.PropsAware;

import java.time.Duration;
import java.time.Instant;

/**
 * @author : sechan
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  /**
   * epoch milliseconds
   */
  @Getter
  private Long exp;

  /**
   * epoch milliseconds
   */
  @Getter
  private Long iat;


  private RefreshToken(Instant iat, Instant exp) {
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
