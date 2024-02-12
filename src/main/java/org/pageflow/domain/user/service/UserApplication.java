package org.pageflow.domain.user.service;

import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.infra.jwt.dto.AccessTokenDto;

/**
 * @author : sechan
 */
public interface UserApplication {
    
    Account signup(SignupForm form, ProviderType provider, RoleType userRole);
    
    LoginTokens login(String username, String password);
    record LoginTokens(String accessToken, long accessTokenExpiredAt, String refreshTokenUUID){}
    
    void logout(String refreshToken);
    
    AccessTokenDto refresh(String refreshTokesnId);

}
