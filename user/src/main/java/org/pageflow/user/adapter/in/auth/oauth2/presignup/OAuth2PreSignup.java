package org.pageflow.user.adapter.in.auth.oauth2.presignup;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.pageflow.common.shared.jpa.TemporaryEntity;
import org.pageflow.common.user.ProviderType;

/**
 * <p>OAuth2 회원가입시 2번의 요청에 걸쳐서 회원가입된다.</p>
 * <p>이때 사용자가 서버에서 지정해준 값을 임의로 변경하는 것을 막기 위하여 임시로 저장되는 사용자 회원가입 form 데이터.</p>
 *
 * @author : sechan
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("oauth2_presignup")
public class OAuth2PreSignup extends TemporaryEntity<OAuth2PreSignup.Data> {

  @Value
  public static class Data {
    ProviderType provider;
    String profileImageUrl;
  }

  public static final Long EXPIRED_MILLIS = 1000 * 60 * 10L; // 10분

  private OAuth2PreSignup(String username, OAuth2PreSignup.Data data, Long expiredAt) {
    super(username, data, expiredAt);
  }

  public static OAuth2PreSignup of(String username, ProviderType provider, String profileImageUrl) {
    return new OAuth2PreSignup(
      username,
      new Data(provider, profileImageUrl),
      System.currentTimeMillis() + EXPIRED_MILLIS
    );
  }

  @Override
  protected Class<Data> getDataClassType() {
    return Data.class;
  }

}
