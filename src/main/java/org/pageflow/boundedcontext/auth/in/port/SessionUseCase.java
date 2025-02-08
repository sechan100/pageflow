package org.pageflow.boundedcontext.auth.in.port;

import org.pageflow.boundedcontext.auth.dto.TokenDto;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SessionUseCase {

  TokenDto.AuthTokens login(LoginCmd cmd);

  TokenDto.AccessToken refresh(UUID sessionId);

  void logout(UUID sessionId);
}