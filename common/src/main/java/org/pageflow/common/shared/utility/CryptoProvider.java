package org.pageflow.common.shared.utility;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@SuppressWarnings("MagicNumber")
public class CryptoProvider {
  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
  private final SecretKeySpec secretKey;

  public CryptoProvider(String key) {
    this.secretKey = createSecretKey(key);
  }

  public String encrypt(String data) {
    IvParameterSpec ivParameterSpec = createIv();
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
      byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encryptedBytes) + ":" + Base64.getEncoder().encodeToString(ivParameterSpec.getIV());
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  public String decrypt(String encryptedData) {
    String[] parts = encryptedData.split(":");
    byte[] decodedBytes = Base64.getDecoder().decode(parts[0]);
    byte[] iv = Base64.getDecoder().decode(parts[1]);

    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
      byte[] decryptedBytes = cipher.doFinal(decodedBytes);
      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  private static SecretKeySpec createSecretKey(String key) {
    byte[] keyBytes = new byte[16]; // AES 128-bit key
    System.arraycopy(
      key.getBytes(StandardCharsets.UTF_8),
      0,
      keyBytes,
      0,
      Math.min(key.getBytes(StandardCharsets.UTF_8).length, keyBytes.length)
    );
    return new SecretKeySpec(keyBytes, ALGORITHM);
  }

  private static IvParameterSpec createIv() {
    byte[] iv = new byte[16]; // AES block size
    new SecureRandom().nextBytes(iv);
    return new IvParameterSpec(iv);
  }
}