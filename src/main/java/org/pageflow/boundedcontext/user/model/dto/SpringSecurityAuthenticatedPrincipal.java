package org.pageflow.boundedcontext.user.model.dto;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SpringSecurityAuthenticatedPrincipal extends AuthenticatedPrincipal {
    
    Collection<? extends GrantedAuthority> getAuthorities();
}
