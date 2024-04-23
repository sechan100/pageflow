package org.pageflow.boundedcontext.auth.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.pageflow.boundedcontext.auth.application.dto.Principal;
import org.pageflow.boundedcontext.auth.application.dto.Token;
import org.pageflow.boundedcontext.auth.domain.AccessToken;
import org.pageflow.boundedcontext.auth.domain.Session;
import org.pageflow.global.config.MapStructConfig;

/**
 * @author : sechan
 */
@Mapper(config = MapStructConfig.class)
public interface AuthMapper {
    @Named("mapTokenCompact")
    default String mapTokenCompact(AccessToken token) {
        return token.compact();
    }



    @Mapping(target = "compact", source = ".", qualifiedByName = "mapTokenCompact")
    Token.AccessTokenDto dtoFromAccessToken(AccessToken accessToken);

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "exp", source = "session.refreshToken.exp")
    Token.RefreshTokenDto refreshTokenDtoFromSession(Session session);


    Principal.Session principalSession_accessToken(AccessToken accessToken);
}
