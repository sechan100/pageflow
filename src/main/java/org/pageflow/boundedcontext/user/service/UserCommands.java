package org.pageflow.boundedcontext.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.user.command.SignupCmd;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.pageflow.boundedcontext.user.entity.ProfileEntity;
import org.pageflow.boundedcontext.user.dto.utils.EncodedPassword;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.pageflow.boundedcontext.user.repository.ProfileRepository;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.shared.annotation.CommandService;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;


@Slf4j
@CommandService
@RequiredArgsConstructor
public class UserCommands {

    private final CustomProps props;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final UserValidator validator;


    @EventListener
    public void signup(SignupCmd cmd){
        SignupForm form = cmd.getForm();

        // 유효성 검사
        validator.validateUsername(form.getUsername());
        validator.validateEmail(form.getEmail());
        validator.validatePassword(form.getPassword());
        validator.validatePenname(form.getPenname());

        // 프로필 사진을 등록하지 않은 경우, 설정값에 저장된 기본 이미지url을 할당함.
        String profileImgUrlOrFallback = Objects.requireNonNullElse(
            form.getProfileImgUrl(),
            props.defaults().userProfileImg()
        );
        // 프로필 생성
        ProfileEntity profile = new ProfileEntity(form.getPenname(), profileImgUrlOrFallback);

        // 계정 생성
        AccountEntity account = new AccountEntity(
            form.getId(),
            form.getUsername(),
            new EncodedPassword(passwordEncoder.encode(form.getPassword())),
            form.getEmail(),
            form.getProvider(),
            form.getRoleType()
        );

        // 연관관계의 주인인 Profile이 @MapsId를 사용해서 Account의 PK를 참조하기 때문에, 반드시 Account가 먼저 영속화 되어야한다.
        AccountEntity savedAccount = accountRepository.save(account);
        profile.associateAccount(savedAccount);
        ProfileEntity savedProfile = profileRepository.save(profile);
    }
}
