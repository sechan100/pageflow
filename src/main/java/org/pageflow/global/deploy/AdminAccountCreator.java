package org.pageflow.global.deploy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.boundedcontext.user.service.AdminDomain;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminAccountCreator {
    
    private final AdminDomain adminDomain;
    private final AccountRepo accountRepo;
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
        if(accountRepo.existsByRole(RoleType.ROLE_ADMIN)) {
            return;
        }
        
        log.info("====== 관리자 계정을 생성합니다. ======");
        
        CustomProps.Admin adminProps = props.admin();
        
        SignupForm adminForm = SignupForm.builder()
                .username(adminProps.username())
                .password(adminProps.password())
                .email(adminProps.email())
                .penname("관리자")
                .build();
        adminDomain.adminSignup(adminForm);
        
        log.info("====== 관리자 계정을 생성했습니다. ======");
    }
    
    
    
    
    
}