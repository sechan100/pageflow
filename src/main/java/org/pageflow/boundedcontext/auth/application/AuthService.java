package org.pageflow.boundedcontext.auth.application;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.SessionManager;
import org.pageflow.boundedcontext.auth.domain.exception.SessionExpiredException;
import org.pageflow.boundedcontext.auth.domain.token.AccessToken;
import org.pageflow.boundedcontext.auth.dto.Principal;
import org.pageflow.boundedcontext.auth.dto.TokenDto;
import org.pageflow.boundedcontext.auth.in.port.LoginCmd;
import org.pageflow.boundedcontext.auth.in.port.SessionUseCase;
import org.pageflow.boundedcontext.auth.in.port.TokenUseCase;
import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
import org.pageflow.boundedcontext.auth.port.out.SessionPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements SessionUseCase, TokenUseCase {

  private final SessionPersistencePort sessionPersistencePort;
  private final AccountPersistencePort accountAcl;

  @Override
  public TokenDto.AuthTokens login(LoginCmd cmd) {
    Account account = cmd.getAleadyAuthedAccount();

    // 세션 로그인 후 영속
    SessionManager session = SessionManager.login(account.getUid(), account.getRole());
    sessionPersistencePort.save(session);

    // 최초 AccessToken 발급(refresh)
    AccessToken accessToken = session.refresh();

    return new TokenDto.AuthTokens(
      toAccessTokenDto(accessToken),
      toRefreshTokenDto(session)
    );
  }

  @Override
  public TokenDto.AccessToken refresh(UUID sessionId) {
    // 세션 로드. 조회 실패시, scheduling에 의해 삭제된 '만료세션'으로 간주.
    SessionManager session = sessionPersistencePort.load(sessionId)
      .orElseThrow(() -> new SessionExpiredException(sessionId));

    // 새 토큰을 발급
    AccessToken at = session.refresh();
    return mapper.dtoFromAccessToken(at);
  }

  @Override
  public void logout(UUID sessionId) {
    // 만약, 해당 세션이 이미 존재하지 않아서 삭제할 수 없는 경우, 그냥 아무동작을 하지 않는다.(이미 삭제된 것과 마찬가지)
    sessionPersistencePort.delete(sessionId);
  }

  @Override
  public Principal.Session extractPrincipalFromAccessToken(String accessTokenCompact) {
    AccessToken accessToken = AccessToken.parse(accessTokenCompact);
    return mapper.principalSession_accessToken(accessToken);
  }


  private TokenDto.AccessToken toAccessTokenDto(AccessToken accessToken) {
    return new TokenDto.AccessToken(
      accessToken.compact(),
      accessToken.getExp()
    );
  }

  private TokenDto.RefreshToken toRefreshTokenDto(SessionManager session) {
    return new TokenDto.RefreshToken(
      session.getId(),
      session.getRefreshTokenIssuer().getExp()
    );
  }

}
