package org.pageflow.boundedcontext.auth.in.port;

import org.pageflow.boundedcontext.auth.dto.Principal;

/**
 * @author : sechan
 */
public interface TokenUseCase {
  Principal.Session extractPrincipalFromAccessToken(String accessTokenCompact);
}
