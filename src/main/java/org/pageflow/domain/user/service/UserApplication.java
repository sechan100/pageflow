package org.pageflow.domain.user.service;

import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.dto.SignupForm;
import org.pageflow.domain.user.model.token.AccessToken;
import org.pageflow.domain.user.model.token.AuthTokens;
import org.pageflow.domain.user.model.user.AggregateUser;

/**
 * @author : sechan
 */
public interface UserApplication {
    
    AggregateUser signup(SignupForm form, ProviderType provider, RoleType userRole);
    
    AuthTokens login(String username, String password);
    
    void logout(String refreshToken);
    
    AccessToken refresh(String refreshTokesnId);
    
    

}
