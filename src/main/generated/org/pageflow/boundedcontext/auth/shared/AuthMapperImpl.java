package org.pageflow.boundedcontext.auth.shared;

import java.time.Instant;
import javax.annotation.processing.Generated;
import org.pageflow.boundedcontext.auth.application.dto.Principal;
import org.pageflow.boundedcontext.auth.application.dto.Token;
import org.pageflow.boundedcontext.auth.domain.AccessToken;
import org.pageflow.boundedcontext.auth.domain.RefreshToken;
import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.boundedcontext.auth.domain.SessionId;
import org.pageflow.boundedcontext.user.domain.UID;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-23T12:21:57+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Homebrew)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public Token.AccessTokenDto dtoFromAccessToken(AccessToken accessToken) {
        if ( accessToken == null ) {
            return null;
        }

        String compact = null;
        Instant exp = null;

        compact = mapTokenCompact( accessToken );
        exp = accessToken.getExp();

        Token.AccessTokenDto accessTokenDto = new Token.AccessTokenDto( compact, exp );

        return accessTokenDto;
    }

    @Override
    public Token.RefreshTokenDto refreshTokenDtoFromSession(Session session) {
        if ( session == null ) {
            return null;
        }

        SessionId sessionId = null;
        Instant exp = null;

        sessionId = session.getId();
        exp = sessionRefreshTokenExp( session );

        Token.RefreshTokenDto refreshTokenDto = new Token.RefreshTokenDto( sessionId, exp );

        return refreshTokenDto;
    }

    @Override
    public Principal.Session principalSession_accessToken(AccessToken accessToken) {
        if ( accessToken == null ) {
            return null;
        }

        UID uid = null;
        RoleType role = null;

        uid = accessToken.getUid();
        role = accessToken.getRole();

        Principal.Session session = new Principal.Session( uid, role );

        return session;
    }

    private Instant sessionRefreshTokenExp(Session session) {
        if ( session == null ) {
            return null;
        }
        RefreshToken refreshToken = session.getRefreshToken();
        if ( refreshToken == null ) {
            return null;
        }
        Instant exp = refreshToken.getExp();
        if ( exp == null ) {
            return null;
        }
        return exp;
    }
}
