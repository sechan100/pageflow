package org.pageflow.boundedcontext.auth.springsecurity.oauth2;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.dto.Principal;
import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
import org.pageflow.boundedcontext.auth.springsecurity.common.InAuthingInFilterForwardFactory;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.GithubOwner;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.GoogleOwner;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.NaverOwner;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.OAuth2ResourceOwner;
import org.pageflow.global.filter.InFilterForwardManager;
import org.pageflow.shared.utility.Forward;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Spring Security -> OAuth2 access token으로 인증한 사용자의 정보를 가져와 처리하는 규격을 커스텀 구현
 */
@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final InFilterForwardManager inFilterForwardManager;
  private final AccountPersistencePort accountAcl;


  /**
   * @param userRequest the user request
   */
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    ClientRegistration clientRegistration = userRequest.getClientRegistration();
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    // social login provider 별로 인증객체를 분리하여 처리
    OAuth2ResourceOwner owner = convertToResourceOwnerImpl(oAuth2User, clientRegistration);
    Optional<Account> loaded = accountAcl.loadAccount(owner.getUsername());

    // case 1) 회원정보가 이미 존재하는 경우 -> 로그인처리
    if(loaded.isPresent()){
      Forward loginForward = InAuthingInFilterForwardFactory.getLoginFoward(loaded.get());
      inFilterForwardManager.inFilterForward(this, loginForward);
      // case 2) 신규 회원가입 처리
    } else {
      // pre-signup을 위한 forward로 위임
      Forward oauth2PreSignupForward = InAuthingInFilterForwardFactory.getOAuth2PreSignupForward(owner);
      inFilterForwardManager.inFilterForward(this, oauth2PreSignupForward);
    }

    // security 스펙상, 제대로 반환을 안하면 AuthenticationException이 발생하므로, 더미객체를 반환.
    return Principal.OnlyInAuthing.dummy();
  }

  // 표준화 되지 않은 OAtuh2User를 리소스서버별로 특화된 ResourceOwner 구현체로 변환
  private OAuth2ResourceOwner convertToResourceOwnerImpl(OAuth2User oAuth2User, ClientRegistration clientRegistration) {

    String registrationId = clientRegistration.getRegistrationId();
        
        /* 하지만 DefaultOAuth2UserService의 loadUser 메서드에서
             이미 OAuth2AuthenticationException로 모든 예외를 처리하고있기 때문에 아래 예외는 발생하지 않는다. */
    return switch(registrationId){
      case "naver" -> new NaverOwner(oAuth2User, clientRegistration);
      case "google" -> new GoogleOwner(oAuth2User, clientRegistration);
      case "github" -> new GithubOwner(oAuth2User, clientRegistration);
      default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
    };
  }
}

