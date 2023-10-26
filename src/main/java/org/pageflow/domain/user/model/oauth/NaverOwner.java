package org.pageflow.domain.user.model.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class NaverOwner extends OAuth2ProviderUser {
    
    public NaverOwner(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super((Map<String, Object>) oAuth2User.getAttributes().get("response"), oAuth2User, clientRegistration);
    }
    
    @Override
    public String getId() {
        return getProvider() + "-" + ((String)getAttributes().get("id"));
    }
}
