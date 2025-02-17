package org.pageflow.user.domain.token;

import lombok.Getter;
import org.pageflow.common.property.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtSignKey {
  @Getter
  private final Key signKey;

  @Autowired
  public JwtSignKey(ApplicationProperties properties) {
    byte[] decodedKey = properties.auth.jwtSecret.getBytes(StandardCharsets.UTF_8);
    this.signKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
  }
}