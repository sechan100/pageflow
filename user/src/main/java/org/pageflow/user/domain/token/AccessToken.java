package org.pageflow.user.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Value;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Value
public final class AccessToken {

  public static final String ROLE_CLAIM_KEY = "rol";
  public static final String SESSION_ID_CLAIM_KEY = "sid";

  UUID sessionId;
  UID uid;
  RoleType role;
  Instant iat;
  Instant exp;
  Key SIGN_KEY;


  /**
   * 토큰을 compact하여 반환한다.
   * @return
   */
  public String compact() {
    Claims clms = Jwts.claims();

    clms.setSubject(uid.toString()) // "sub": "DKG2325EDdgG"
      .setIssuedAt(Date.from(this.iat))      // "iat": 1516239022
      .setExpiration(Date.from(this.exp));    // "exp": 1516239022
    clms.put(SESSION_ID_CLAIM_KEY, sessionId.toString()); // "sid": "DKG2325EDdgG"
    clms.put(ROLE_CLAIM_KEY, role.name()); // "rol": "ROLE_USER"

    return Jwts.builder()
      .setClaims(clms)
      .signWith(SIGN_KEY)
      .compact();
  }

}