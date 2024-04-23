package org.pageflow.boundedcontext.auth.application.springsecurity.oauth2.owner;


import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractOwner implements OAuth2ResourceOwner {

    private final OAuth2User oAuth2User;
    private final ClientRegistration clientRegistration;
    private final Map<String, Object> attributes;

    public AbstractOwner(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
        this.attributes = Collections.unmodifiableMap(attributes);
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }

    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }

    // format: '${provider}-${identifier}'   ex)GOOGLE-14223456829527890
    @Override
    public String getUsername() {
        return getProviderType() + "-" + getId();
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
