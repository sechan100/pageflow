package org.pageflow.boundedcontext.auth.application.springsecurity.oauth2.owner;

import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GithubOwner extends AbstractOwner {


    public GithubOwner(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("id");
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
