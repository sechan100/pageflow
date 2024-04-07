package org.pageflow.boundedcontext.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.cache.OAuth2PresignupCache;
import org.pageflow.boundedcontext.user.dto.oauth.GithubOwner;
import org.pageflow.boundedcontext.user.dto.oauth.GoogleOwner;
import org.pageflow.boundedcontext.user.dto.oauth.NaverOwner;
import org.pageflow.boundedcontext.user.dto.oauth.ResourceOwner;
import org.pageflow.boundedcontext.user.dto.principal.OnlyAuthProcessPrincipal;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.pageflow.boundedcontext.user.repository.OAuth2PresignupCacheRepository;
import org.pageflow.global.api.Forwarder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Spring Security -> OAuth2 access token으로 인증한 사용자의 정보를 가져와 처리하는 규격을 커스텀 구현
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final AccountRepository accountRepository;
    private final OAuth2PresignupCacheRepository presignupRepo;
    private final Forwarder forwarder;
    
    /**
     * @param userRequest the user request
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // social login provider 별로 인증객체를 분리하여 처리
        ResourceOwner resourceOwner = convertToResourceOwnerImpl(oAuth2User, clientRegistration);
        
        // case 1) 회원정보가 이미 존재하여 로그인 처리
        if(accountRepository.existsByUsername(resourceOwner.getUsername())) {
            forwarder.forwardBuilder("/internal/oauth2/login")
                .param("username", resourceOwner.getUsername())
                .forward();
            
        // case 2) 신규 회원가입 처리
        } else {
            boolean isAlreadyPresignuped = presignupRepo.existsById(resourceOwner.getUsername());
            // pre-signup 기록이 없다면 캐시에 저장
            if(!isAlreadyPresignuped){
                presignupRepo.save(new OAuth2PresignupCache(
                    resourceOwner.getUsername(),
                    resourceOwner.getProviderType(),
                    resourceOwner.getNickname(),
                    resourceOwner.getEmail(),
                    resourceOwner.getProfileImgUrl())
                );
            }
            // pre-signup 기록을 반환하는 controller로 위임
            forwarder.forwardBuilder("/internal/pre-signup")
                    .param("username", resourceOwner.getUsername())
                    .forward();
        }

        // security 스펙상, 제대로 반환을 안하면 AuthenticationException이 발생하므로, 더미객체를 반환.
        return OnlyAuthProcessPrincipal.dummy();
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

