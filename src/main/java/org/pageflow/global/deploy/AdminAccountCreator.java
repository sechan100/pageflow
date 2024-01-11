package org.pageflow.global.deploy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.model.dto.SignupForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.pageflow.domain.user.service.AdminUserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminAccountCreator {
    
    private final AdminUserService adminUserService;
    private final AccountRepository accountRepository;
    private final CustomProps customProps;

    
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
        
        CustomProps.Admin adminProps = customProps.getAdmin();
        
        SignupForm adminForm = SignupForm.builder()
                .username(adminProps.getUsername())
                .password(adminProps.getPassword())
                .passwordConfirm(adminProps.getPassword())
                .email(adminProps.getEmail())
                .penname("관리자")
                .build();
        adminUserService.adminSignup(adminForm);
        
        log.info("====== 관리자 계정을 생성했습니다. ======");
    }
    
    
    
    
    
}