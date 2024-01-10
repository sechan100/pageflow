package org.pageflow.domain.user.service;

import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.infra.jwt.token.AccessToken;
import org.pageflow.infra.jwt.token.SessionToken;

import java.util.Map;

/**
 * @author : sechan
 */
public interface UserApplication {
    
    Account signup(SignupForm form, ProviderType provider, RoleType userRole);
    /**
     * @return accessToken, refreshToken
     */
    Map<String, SessionToken> login(String username, String password);
    void logout(String refreshToken);
    AccessToken refresh(String refreshToken);
}
