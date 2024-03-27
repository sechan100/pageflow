package org.pageflow.boundedcontext.user.model.oauth;

import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class NaverOwner extends DefaultResourceOwner {

    public NaverOwner(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super((Map<String, Object>) oAuth2User.getAttributes().get("response"), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("id");
    }

    @Override
    public String getProfileImgUrl() {
        return (String) getAttributes().get("profile_image");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("nickname");
    }
    
    @Override
    public ProviderType getProviderType() {
        return ProviderType.NAVER;
    }
}