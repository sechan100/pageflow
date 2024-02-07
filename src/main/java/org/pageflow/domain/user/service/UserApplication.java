package org.pageflow.domain.user.service;

import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.infra.jwt.provider.JwtProvider;

/**
 * @author : sechan
 */
public interface UserApplication {
    
    Account signup(SignupForm form, ProviderType provider, RoleType userRole);
    
    ClientAspectAuthResults login(String username, String password);
    record ClientAspectAuthResults(String accessToken, long accessTokenExpiredAt, String refreshTokenUUID){}
    
    void logout(String refreshToken);
    
    JwtProvider.AccessTokenReturn refresh(String refreshTokesnId);

}
