package org.pageflow.user.domain.token;

import lombok.Value;
import org.pageflow.common.property.PropsAware;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Value
public class JwtSignKey {
  Key signKey;

  public JwtSignKey() {
    byte[] decodedKey = PropsAware.use().auth.jwtSecret.getBytes(StandardCharsets.UTF_8);
    this.signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
  }
}