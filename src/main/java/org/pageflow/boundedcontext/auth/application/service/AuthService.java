package org.pageflow.boundedcontext.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.application.acl.LoadAccountAcl;
import org.pageflow.boundedcontext.auth.application.dto.Principal;
import org.pageflow.boundedcontext.auth.application.dto.Token;
import org.pageflow.boundedcontext.auth.domain.AccessToken;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.auth.port.in.LoginCmd;
import org.pageflow.boundedcontext.auth.port.in.SessionUseCase;
import org.pageflow.boundedcontext.auth.port.in.TokenUseCase;
import org.pageflow.boundedcontext.auth.port.out.CmdSessionPort;
import org.pageflow.boundedcontext.auth.port.out.LoadSessionPort;
import org.pageflow.boundedcontext.auth.shared.AuthMapper;
import org.pageflow.global.api.code.Code1;
import org.pageflow.shared.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@UseCase
@Transactional
@RequiredArgsConstructor
public class AuthService implements SessionUseCase, TokenUseCase {

    private final CmdSessionPort cmdSessionPort;
    private final LoadSessionPort loadSessionPort;
    private final LoadAccountAcl loadAccountAcl;
    private final AuthMapper mapper;

    @Override
    public Token.AuthTokens login(LoginCmd cmd) {
        Account account = cmd.getAleadyAuthedAccount();

        // 세션 로그인 후 영속
        Session session = Session.login(account.getUid(), account.getRole());
        cmdSessionPort.save(session);

        // 최초 AccessToken 발급(refresh)
        AccessToken accessToken = session.refresh();

        return new Token.AuthTokens(
            mapper.dtoFromAccessToken(accessToken),
            mapper.refreshTokenDtoFromSession(session)
        );
    }

    @Override
    public Token.AccessTokenDto refresh(SessionId sessionId){
        // 세션 로드. 조회 실패시, scheduling에 의해 삭제된 '만료세션'으로 간주.
        Session session = loadSessionPort.load(sessionId).orElseThrow(
            Code1.SESSION_EXPIRED::fire
        );

        // 새 토큰을 발급
        AccessToken at = session.refresh();
        return mapper.dtoFromAccessToken(at);
    }

    @Override
    public void logout(SessionId sessionId){
        // 만약, 해당 세션이 이미 존재하지 않아서 삭제할 수 없는 경우, 그냥 아무동작을 하지 않는다.(이미 삭제된 것과 마찬가지)
        cmdSessionPort.delete(sessionId);
    }

    @Override
    public Principal.Session parseAndGetSession(String accessTokenCompact) {
        AccessToken accessToken = AccessToken.parse(accessTokenCompact);
        return mapper.principalSession_accessToken(accessToken);
    }
}
