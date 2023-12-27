package org.pageflow.domain.user.jwt;

import lombok.Builder;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiredIn;
}
