package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.user.constants.Role;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.model.dto.PrincipalContext;
import org.pageflow.domain.user.model.oauth.GoogleOwner;
import org.pageflow.domain.user.model.oauth.NaverOwner;
import org.pageflow.domain.user.model.oauth.ResourceOwner;
import org.pageflow.infra.util.Ut;
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
    
    private final AccountService accountService;
    private final AwaitingVerificationEmailService emailCacheService;
    private final Rq rq;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        
        // social login provider 별로 인증객체를 분리하여 처리
        ResourceOwner proviverUser = providerUser(oAuth2User, clientRegistration);
        AdditionalSignupAccountDto registerForm = new AdditionalSignupAccountDto(proviverUser);
        
        
        // 기존 회원정보 존재 -> 회원정보를 업데이트 후, 로그인 진행
        if(accountService.existsByUsername(registerForm.getUsername())){
            accountService.updateAccount(registerForm);
            Account refrashedAccount = accountService.findByUsernameWithProfile(registerForm.getUsername());
            return new PrincipalContext(refrashedAccount);
            
        // 회원정보 없음(신규) -> 기본 Account 정보가 포함된 AccountDetilasRegisterForm을 redis에 저장하고 리다이렉트
        } else {
            String authenticationCode = Ut.generator.generateRandomString();
            emailCacheService.save(new AwaitingEmailVerificationRequest(registerForm, authenticationCode, true));
            rq.redirect("/signup?code=" + authenticationCode + "&email=" + registerForm.getEmail());
            return new PrincipalContext(Account.builder().username("anonymous").password("anonymous").role(Role.ANONYMOUS).build());
        }
    }
    
    
    // 표준화 되지 않은 OAtuh2User를 리소스서버별로 특화된 ProviderUser 구현체로 변환
    private ResourceOwner providerUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        
        String registrationId = clientRegistration.getRegistrationId();
        
        if (registrationId.equals("naver")) {
            return new NaverOwner(oAuth2User, clientRegistration);
            
        } else if (registrationId.equals("google")) {
            return new GoogleOwner(oAuth2User, clientRegistration);
            
        } else {
            /* 하지만 DefaultOAuth2UserService의 loadUser 메서드에서
             이미 OAuth2AuthenticationException로 모든 예외를 처리하고있기 때문에 아래 예외는 발생하지 않는다. */
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }
    }
}
