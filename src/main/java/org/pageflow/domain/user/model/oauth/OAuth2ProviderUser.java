package org.pageflow.domain.user.model.oauth;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class OAuth2ProviderUser implements ResourceOwner {
    
    private final OAuth2User oAuth2User;
    private final ClientRegistration clientRegistration;
    private final Map<String, Object> attributes;
    
    public OAuth2ProviderUser(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
        this.attributes = attributes;
    }
    
    @Override
    public String getPassword() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }
    
    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }
    
    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
