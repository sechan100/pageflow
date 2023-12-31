package org.pageflow.domain.user.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.SignupCache;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.model.dto.UserDto;
import org.pageflow.domain.user.repository.SignupCacheRepository;
import org.pageflow.domain.user.service.DefaultUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Transactional
@Tag(name = "User", description = "사용자 API")
public class SignupController {

    private final Rq rq;
    private final SignupCacheRepository signupCacheRepository;
    private final DefaultUserService defaultUserService;
    
    
    
    @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
    @PostMapping("/signup")
    public UserDto signup(@Valid @RequestBody SignupForm form) {
        
        // 캐싱된 회원가입 데이터가 존재하는지 확인 -> 존재하면 OAuth2로 가입한 사용자
        boolean isOAuth = signupCacheRepository.existsById(form.getUsername());
        Account savedAccount;
        
        // OAuth2 회원가입
        if(isOAuth){
            // 캐시 가져옴
            SignupCache cache = signupCacheRepository.findById(form.getUsername()).orElseThrow();
            
            // 사용자 등록(사용자가 임의로 변경해선 안되는 데이터는 캐시에서 지정)
            savedAccount = defaultUserService.signup(form, cache.getProvider(), RoleType.ROLE_USER);
            
            // 캐싱된 회원가입 데이터를 삭제
            signupCacheRepository.deleteById(form.getUsername());
        }
        // 일반 회원가입
        else {
            // 사용자 등록
            savedAccount = defaultUserService.signup(form, ProviderType.NATIVE, RoleType.ROLE_USER);
        }
        
        // 사용자 데이터 반환
        return UserDto.from(savedAccount);
    }
    
    
    @Hidden
    @GetMapping("/internal/signup/cache")
    public SignupForm signupCache(@RequestParam("username") String username) {
        SignupCache cache = signupCacheRepository.findById(username).orElseThrow();
        
        String radomPassword = UUID.randomUUID().toString();
        return SignupForm.builder()
                .username(cache.getUsername())
                .password(radomPassword)
                .passwordConfirm(radomPassword)
                .email(cache.getEmail())
                .penname(cache.getPenname())
                .profileImgUrl(cache.getProfileImgUrl())
                .build();
    }
    
}
