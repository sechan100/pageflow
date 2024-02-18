package org.pageflow.domain.user.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.dto.SignupForm;
import org.pageflow.domain.user.entity.SignupCache;
import org.pageflow.domain.user.model.user.AggregateUser;
import org.pageflow.domain.user.repository.SignupCacheRepository;
import org.pageflow.domain.user.service.UserApplication;
import org.pageflow.global.request.RequestContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Transactional
@Tag(name = "User", description = "사용자 API")
public class SignupController {

    private final RequestContext requestContext;
    private final SignupCacheRepository signupCacheRepository;
    private final UserApplication userApplication;
    
    
    
    @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
    @PostMapping("/signup")
    public SignupedUser signup(@Valid @RequestBody SignupForm form) {
        
        // 캐싱된 회원가입 데이터가 존재하는지 확인 -> 존재하면 OAuth2로 가입한 사용자
        boolean isOAuth = signupCacheRepository.existsById(form.getUsername());
        AggregateUser persistedUser;
        
        // 1. OAuth2 회원가입
        if(isOAuth){
            // 캐시 가져옴
            SignupCache cache = signupCacheRepository.findById(form.getUsername()).orElseThrow();
            
            // 사용자 등록(사용자가 임의로 변경해선 안되는 데이터는 캐시에 있던 값으로 지정: Provider)
            persistedUser = userApplication.signup(form, cache.getProvider(), RoleType.ROLE_USER);
            
            // 캐싱된 회원가입 데이터를 삭제
            signupCacheRepository.deleteById(form.getUsername());
        }
        // 2. 일반 회원가입
        else {
            // 사용자 등록
            persistedUser = userApplication.signup(form, ProviderType.NATIVE, RoleType.ROLE_USER);
        }
        
        // 사용자 데이터 반환
        return SignupedUser.builder()
                .UID(persistedUser.getAccount().getUID())
                .provider(persistedUser.getAccount().getProvider())
                .username(persistedUser.getAccount().getUsername())
                .email(persistedUser.getAccount().getEmail())
                .emailVerified(persistedUser.getAccount().isEmailVerified())
                .role(persistedUser.getAccount().getRole())
                .penname(persistedUser.getProfile().getPenname())
                .build();
    }
    
    
    @Hidden
    @GetMapping("/internal/signup/cache")
    public SignupForm signupCache(@RequestParam("username") String username) {
        SignupCache cache = signupCacheRepository.findById(username).orElseThrow();
        
        String radomPassword = UUID.randomUUID().toString();
        return SignupForm.builder()
                .username(cache.getUsername())
                .password(radomPassword)
                .email(cache.getEmail())
                .penname(cache.getPenname())
                .profileImgUrl(cache.getProfileImgUrl())
                .build();
    }
    
    
    
    
    // RECORDS
    @Builder record SignupedUser(
            Long UID,
            ProviderType provider,
            String username,
            String email,
            boolean emailVerified,
            RoleType role,
            String penname,
            String profileImgUrl
    ) {}
}
