package org.pageflow.boundedcontext.user.application.runtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.AccountJpaRepository;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.AdminUseCase;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.initialize.RuntimeInitializer;
import org.pageflow.global.property.AppProps;
import org.pageflow.shared.utility.JsonUtility;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminAccountCreater implements RuntimeInitializer {
    private final AdminUseCase adminUseCase;
    private final AccountJpaRepository accountJpaRepo;
    private final AppProps props;

    @Override
    @Transactional
    public void initialize() {
        if(accountJpaRepo.existsByRole(RoleType.ROLE_ADMIN)) {
            return;
        }
        AppProps.Admin admin = props.admin;
        SignupCmd cmd = new SignupCmd(
            Username.from(admin.username),
            Password.encrypt(admin.password),
            Email.from(admin.email),
            Penname.from(admin.penname),
            RoleType.ROLE_ADMIN,
            ProviderType.NATIVE,
            ProfileImageUrl.from(props.user.defaultProfileImageUrl)
        );

        UserDto.User result = adminUseCase.registerAdmin(cmd);

        log.info("관리자 계정을 생성했습니다. {}", JsonUtility.toJson(result));
    }
}
