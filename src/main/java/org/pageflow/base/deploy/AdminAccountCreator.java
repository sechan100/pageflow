package org.pageflow.base.deploy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminAccountCreator {
    
    private final AccountService accountService;
    private final CustomProperties customProperties;

    
    @Bean
    public ApplicationRunner init2() {
        return args -> {
            createAdminAccount();
        };
    };
    
    @Transactional
    private void createAdminAccount() {
        boolean isAdminAccountExist = accountService.repoExistsByUsername("admin");
        // 이미 관리자 계정이 존재하면 생성하지 않음
        if(isAdminAccountExist) {
            log.info("====== 관리자 계정이 이미 존재해서 생성하지 않았습니다. ======");
            return;
        }
        
        log.info("====== 관리자 계정을 생성합니다. ======");
        
        CustomProperties.Admin adminProperties = customProperties.getAdmin();
        
        //관리자 계정 생성
        AdditionalSignupAccountDto adminAccountDto = AdditionalSignupAccountDto.builder()
                .username("admin")
                .password(adminProperties.getPassword())
                .email(adminProperties.getEmail())
                .nickname(adminProperties.getNickname())
                .provider(ProviderType.NATIVE)
                .profileImgUrl("/img/pageflow-banner2.PNG")
                .build();
        
        accountService.register(adminAccountDto);
        log.info("====== 관리자 계정 생성완료! ======");
    }
    
    
    
    
    
}