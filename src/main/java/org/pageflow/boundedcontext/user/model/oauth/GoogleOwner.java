package org.pageflow.boundedcontext.user.model.oauth;

import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleOwner extends DefaultResourceOwner {
    
    
    public GoogleOwner(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }
    
    @Override
    public String getId() {
        return (String) getAttributes().get("sub");
    }
    
    @Override
    public String getProfileImgUrl() {
        return (String) getAttributes().get("picture");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("name");
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.GOOGLE;
    }
}
