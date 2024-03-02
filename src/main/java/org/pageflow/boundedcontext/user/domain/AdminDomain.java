package org.pageflow.boundedcontext.user.domain;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.boundedcontext.user.model.user.AggregateUser;
import org.pageflow.boundedcontext.user.service.UserCommander;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.infra.extension.stereotype.Domain;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * @author : sechan
 */
@Domain
@RequiredArgsConstructor
public class AdminDomain {
    
    private final UserCommander userCommander;
    private final CustomProps props;
    private final PasswordEncoder passwordEncoder;
    
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
        
        return userCommander.saveUser(account, profile);
    }
}
