package org.pageflow.user.port.in;

import org.pageflow.common.result.Result;
import org.pageflow.user.domain.token.AccessToken;

/**
 * @author : sechan
 */
public interface TokenUseCase {

  /**
   * compact된 token으로부터 AccessToken 객체를 생성한다.
   *
   * @param accessTokenCompact
   * @return accessToken을 반환한다. 만료된 토큰인 경우 UserCode.ACCESS_TOKEN_EXPIRED의 result를 반환한다.
   */
  Result<AccessToken> parseAccessToken(String accessTokenCompact);

}
