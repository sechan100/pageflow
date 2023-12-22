package org.pageflow.domain.user.model.oauth;

import org.pageflow.domain.user.constants.ProviderType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GithubOwner extends DefaultResourceOwner {


    public GithubOwner(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return getProvider() + "-" + getAttributes().get("id");
    }

    @Override
    public String getProfileImgUrl() {
        return (String) getAttributes().get("avatar_url");
    }

    @Override
    public String getNickname() {
        return (String) getAttributes().get("login");
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.GITHUB;
    }
}
