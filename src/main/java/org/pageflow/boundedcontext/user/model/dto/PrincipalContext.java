package org.pageflow.boundedcontext.user.model.dto;


import lombok.Getter;
import org.pageflow.boundedcontext.user.constants.Role;
import org.pageflow.boundedcontext.user.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;


public class PrincipalContext extends User implements OAuth2User {
    
    @Getter
    protected UserSession userSession;
    
    public PrincipalContext(Account account) {
        super(account.getUsername(), account.getPassword(), Role.getAuthorities(account.getRole()));
        this.userSession = new UserSession(account);
    }
    
    public PrincipalContext(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
    
    @Override
    public String getName() {
        return null;
    }
    
}
