package org.pageflow.user.port.in;

import org.pageflow.user.domain.token.AccessToken;

/**
 * @author : sechan
 */
public interface TokenUseCase {
  AccessToken parseAccessToken(String accessTokenCompact);
}
