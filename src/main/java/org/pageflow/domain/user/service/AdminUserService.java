package org.pageflow.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.model.user.AggregateUser;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.domain.user.dto.SignupForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {
    
    private final AccountRepository accountRepository;
    private final CustomProps props;
    private final PasswordEncoder passwordEncoder;
    private final DefaultUserService defaultUserService;
    private final ProfileRepository profileRepository;
    
    public AggregateUser adminSignup(SignupForm form) {
        
        // 프로필 생성
        Profile profile = Profile.builder()
                .penname(form.getPenname())
                // 프로필 사진을 등록하지 않은 경우, 기본 이미지로 설정한다.
                .profileImgUrl(Objects.requireNonNullElse(form.getProfileImgUrl(), props.defaults().userProfileImg()))
                .build();
        
        // 계정 생성
        Account account = Account.builder()
                .provider(ProviderType.NATIVE)
                .email(form.getEmail())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(RoleType.ROLE_ADMIN)
                .build();
        
        return defaultUserService.saveUser(account, profile);
    }
}
