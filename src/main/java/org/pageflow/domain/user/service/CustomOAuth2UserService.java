package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.SignupCache;
import org.pageflow.domain.user.model.oauth.GithubOwner;
import org.pageflow.domain.user.model.oauth.GoogleOwner;
import org.pageflow.domain.user.model.oauth.NaverOwner;
import org.pageflow.domain.user.model.oauth.ResourceOwner;
import org.pageflow.domain.user.model.principal.InitialAuthenticationPrincipal;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.SignupCacheRepository;
import org.pageflow.global.request.Forwarder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Spring Security -> OAuth2 access token으로 인증한 사용자의 정보를 가져와 처리하는 규격을 커스텀 구현
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultUserService defaultUserService;
    private final AccountRepository accountRepository;
    private final SignupCacheRepository signupCacheRepository;
    private final Forwarder forwarder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // social login provider 별로 인증객체를 분리하여 처리
        ResourceOwner resourceOwner = convertToResourceOwnerImpl(oAuth2User, clientRegistration);

        // 기존 회원정보 존재 -> 기존 정보로 로그인
        if (accountRepository.existsByUsername(resourceOwner.getUsername())) {
            
            Account account = accountRepository.findWithProfileByUsername(resourceOwner.getUsername());
            
            // 포워딩으로 위임
            forwarder.forwardBuilder("/internal/login")
                    .param("username", resourceOwner.getUsername())
                    .param("password", account.getPassword())
                    .forward();
            
        // 회원정보 없음(신규) -> OAuth2 데이터를 username으로 캐싱하고, signup 페이지로 리디렉션
        } else {
            
            // 캐시 존재 여부
            boolean isAlreadyCached = signupCacheRepository.existsById(resourceOwner.getUsername());
            if(!isAlreadyCached){
                // 캐시가 없다면, username으로 회원가입 임시 데이터를 캐싱
                signupCacheRepository.save(
                        SignupCache.builder()
                                .username(resourceOwner.getUsername())
                                .provider(resourceOwner.getProviderType())
                                .penname(resourceOwner.getNickname())
                                .email(resourceOwner.getEmail())
                                .profileImgUrl(resourceOwner.getProfileImgUrl())
                                .build()
                );
            }
            
            // OAuth2 캐시 데이터를 반환
            forwarder.forwardBuilder("/internal/signup/cache")
                    .param("username", resourceOwner.getUsername())
                    .forward();
        }
        
        // security 스펙상, 제대로 반환을 안하면 AuthenticationException이 발생하므로, 빈 객체를 반환
        return InitialAuthenticationPrincipal.anonymous();
    }


    // 표준화 되지 않은 OAtuh2User를 리소스서버별로 특화된 ResourceOwner 구현체로 변환
    private ResourceOwner convertToResourceOwnerImpl(OAuth2User oAuth2User, ClientRegistration clientRegistration) {

        String registrationId = clientRegistration.getRegistrationId();
        
        /* 하지만 DefaultOAuth2UserService의 loadUser 메서드에서
             이미 OAuth2AuthenticationException로 모든 예외를 처리하고있기 때문에 아래 예외는 발생하지 않는다. */
        return switch (registrationId) {
            case "naver" -> new NaverOwner(oAuth2User, clientRegistration);
            case "google" -> new GoogleOwner(oAuth2User, clientRegistration);
            case "github" -> new GithubOwner(oAuth2User, clientRegistration);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        };
    }
}
