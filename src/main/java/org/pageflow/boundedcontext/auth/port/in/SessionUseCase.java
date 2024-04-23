package org.pageflow.boundedcontext.auth.port.in;

import org.pageflow.boundedcontext.auth.application.dto.Token;
import org.pageflow.boundedcontext.auth.domain.SessionId;

/**
 * @author : sechan
 */
public interface SessionUseCase {

    Token.AuthTokens login(LoginCmd cmd);

    Token.AccessTokenDto refresh(SessionId sessionId);

    void logout(SessionId sessionId);
}