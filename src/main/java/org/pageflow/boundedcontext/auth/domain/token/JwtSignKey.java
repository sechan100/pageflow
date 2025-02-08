package org.pageflow.boundedcontext.auth.domain.token;

import lombok.Value;
import org.pageflow.global.property.PropsAware;

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