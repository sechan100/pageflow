package org.pageflow.boundedcontext.user.controller;


import io.hypersistence.tsid.TSID;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.boundedcontext.user.entity.SignupCache;
import org.pageflow.boundedcontext.user.model.user.User;
import org.pageflow.boundedcontext.user.repository.SignupCacheRepo;
import org.pageflow.boundedcontext.user.service.UserService;
import org.pageflow.global.api.GeneralResponse;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.code.UserCode;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final RequestContext requestContext;
    private final SignupCacheRepo signupCacheRepo;
    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
    @PostMapping("/signup")
    public SignupedUser signup(@Valid @RequestBody SignupForm form) {

        // 캐싱된 회원가입 데이터가 존재하는지 확인 -> 존재하면 OAuth2로 가입한 사용자
        boolean isOAuth = signupCacheRepo.existsById(form.getUsername());
        User persistedUser;
        
        // 1. OAuth2 회원가입
        if(isOAuth){
            // 캐시 가져옴
            SignupCache cache = signupCacheRepo.findById(form.getUsername()).get();
            
            // 사용자 등록(사용자가 임의로 변경해선 안되는 데이터는 캐시에 있던 값으로 지정: Provider)
            persistedUser = userService.signup(form, cache.getProvider(), RoleType.ROLE_USER);
            
            // 캐싱된 회원가입 데이터를 삭제
            signupCacheRepo.deleteById(form.getUsername());
        }
        // 2. 일반 회원가입
        else {
            // 사용자 등록
            persistedUser = userService.signup(form, ProviderType.NATIVE, RoleType.ROLE_USER);
        }
        
        // 사용자 데이터 반환
        return SignupedUser.builder()
                .UID(persistedUser.getAccount().getId())
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
    public GeneralResponse<SignupForm> fetchSignupCache(@RequestParam("username") String username) {
        SignupCache cache = signupCacheRepo.findById(username).get();
        
        String radomPassword = UUID.randomUUID().toString();
        SignupForm form = SignupForm.builder()
                .username(cache.getUsername())
                .password(radomPassword)
                .email(cache.getEmail())
                .penname(cache.getPenname())
                .profileImgUrl(cache.getProfileImgUrl())
                .build();
        
        return GeneralResponse.builder()
                .apiCode(UserCode.OAUTH2_SIGNUP_REQUIRED)
                .data(form)
                .build();
    }
    
    
    // RECORDS
    @Builder record SignupedUser(
            TSID UID,
            ProviderType provider,
            String username,
            String email,
            boolean emailVerified,
            RoleType role,
            String penname,
            String profileImgUrl
    ) {}
}
