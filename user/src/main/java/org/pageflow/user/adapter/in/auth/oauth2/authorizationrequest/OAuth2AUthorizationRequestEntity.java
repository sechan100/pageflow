package org.pageflow.user.adapter.in.auth.oauth2.authorizationrequest;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.common.jpa.TemporaryEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 *
 * @author : sechan
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("oauth_authorization_request")
public class OAuth2AUthorizationRequestEntity extends TemporaryEntity<OAuth2AuthorizationRequest> {

  public static final Long EXPIRED_MILLIS = 1000 * 60 * 10L; // 10분

  private OAuth2AUthorizationRequestEntity(String state, OAuth2AuthorizationRequest request, Long expiredAt) {
    super(state, request, expiredAt);
  }

  public static OAuth2AUthorizationRequestEntity of(String state, OAuth2AuthorizationRequest authorizationRequest) {
    return new OAuth2AUthorizationRequestEntity(
      state,
      authorizationRequest,
      System.currentTimeMillis() + EXPIRED_MILLIS
    );
  }

  @Override
  protected Class<OAuth2AuthorizationRequest> getDataClassType() {
    return OAuth2AuthorizationRequest.class;
  }
}
