package org.pageflow.boundedcontext.auth.port.in;

import org.pageflow.boundedcontext.auth.application.dto.Principal;

/**
 * @author : sechan
 */
public interface TokenUseCase {
    Principal.Session extractPrincipalFromAccessToken(String accessTokenCompact);
}
