package org.pageflow.domain.user.model.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.pageflow.domain.user.entity.RefreshToken;

/**
 * @author : sechan
 */
@Data
@AllArgsConstructor
public class AuthTokens {
    private AccessToken accessToken;
    private RefreshToken refreshToken;
}
