package org.pageflow.domain.user.model.dto;


import lombok.Getter;
import org.pageflow.domain.user.constants.Role;
import org.pageflow.domain.user.entity.Account;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


@Getter
public class PrincipalContext extends User implements OAuth2User {

    protected UserSession userSession;

    public PrincipalContext(Account account) {
        super(account.getUsername(), account.getPassword(), Role.getAuthorities(account.getRole()));
        this.userSession = new UserSession(account);
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
