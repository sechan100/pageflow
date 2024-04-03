package org.pageflow.boundedcontext.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.boundedcontext.user.model.user.User;
import org.pageflow.boundedcontext.user.model.utils.EncodedPassword;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.boundedcontext.user.repository.ProfileRepo;
import org.pageflow.boundedcontext.user.repository.RefreshTokenRepo;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.infra.jwt.provider.JwtProvider;
import org.pageflow.shared.annotation.TransactionalService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;


@Slf4j
@TransactionalService
@RequiredArgsConstructor
public class UserService {

    private final CustomProps props;
    private final AccountRepo accountRepo;
    private final ProfileRepo profileRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtProvider jwtProvider;
    private final UtilityUserService userUtil;
    private final AuthService authService;
    private final UserCommander userCommander;


    /**
     * 회원가입.
     * form과 OAuth2에 관계없이 SignupForm 객체의 정보를 기반으로 Account와 Profile을 생성한다.
     */
    public User signup(SignupForm form, ProviderType provider, RoleType userRole){

        // username 검사
        userUtil.validateUsername(form.getUsername());
        // email 검사
        userUtil.validateEmail(form.getEmail());
        // password 검사
        userUtil.validatePassword(form.getPassword());
        // penname 검사
        userUtil.validatePenname(form.getPenname());

        // 프로필 생성
        Profile profile = Profile.builder().penname(form.getPenname())
            // 프로필 사진을 등록하지 않은 경우, 설정값에 저장된 기본 이미지url을 할당함.
            .profileImgUrl(Objects.requireNonNullElse(
                form.getProfileImgUrl(),
                props.defaults().userProfileImg())
            ).build();

        // 계정 생성
        Account account = new Account(
            form.getUsername(),
            new EncodedPassword(passwordEncoder.encode(form.getPassword())),
            form.getEmail(),
            provider,
            userRole
        );

        return userCommander.saveUser(account, profile);
    }

}
