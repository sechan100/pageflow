package org.pageflow.domain.user.service;

import org.junit.jupiter.api.Test;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.model.dto.BasicSignupAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sechan
 */
@SpringBootTest
class AccountServiceTest {
    
    @Autowired
    private AccountService accountService;
    
    @Test
    @Commit
    void registerAdmin() {
        AccountDto basicSignupAccountDto = BasicSignupAccountDto.builder()
                .provider(ProviderType.NATIVE)
                .username("admin")
                .password("admin")
                .email("sechan100@gmail.com")
                .build();
                
                
        AdditionalSignupAccountDto additionalSignupAccountDto = new AdditionalSignupAccountDto(basicSignupAccountDto);
        additionalSignupAccountDto.setNickname("ADMINISTRATOR");
        accountService.register(additionalSignupAccountDto);
    }
}