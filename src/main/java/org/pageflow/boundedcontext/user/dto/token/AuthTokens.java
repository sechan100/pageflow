package org.pageflow.boundedcontext.user.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.pageflow.boundedcontext.user.entity.RefreshToken;

/**
 * @author : sechan
 */
@Data
@AllArgsConstructor
public class AuthTokens {
    private AccessToken accessToken;
    private RefreshToken refreshToken;
}
