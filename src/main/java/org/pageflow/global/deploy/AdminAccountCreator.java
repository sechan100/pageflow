package org.pageflow.global.deploy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.domain.User;
import org.pageflow.boundedcontext.user.usecase.UserUsecase;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.ApiRevealSignupForm;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminAccountCreator {

    private final UserUsecase userUsecase;
    private final AccountRepository accountRepository;
    private final CustomProps props;

    
    @Bean
    public ApplicationRunner init2() {
        return args -> {
            createAdminAccount();
        };
    }
    
    @Transactional
    private void createAdminAccount() {
        // 이미 관리자 계정이 존재하면 생성하지 않음
        if(accountRepository.existsByRole(RoleType.ROLE_ADMIN)) {
            return;
        }
        log.info("====== 관리자 계정을 생성합니다. ======");
        CustomProps.Admin adminProps = props.admin();
        ApiRevealSignupForm adminForm = ApiRevealSignupForm.builder()
                .username(adminProps.username())
                .password(adminProps.password())
                .email(adminProps.email())
                .penname("관리자")
                .build();

        User.signup(adminForm, ProviderType.NATIVE, RoleType.ROLE_ADMIN);
        log.info("====== 관리자 계정을 생성했습니다. ======");
    }
    
    
    
    
    
}