package org.pageflow.boundedcontext.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sechan
 */
@SpringBootTest
class UserAuthServiceTest {
    
    @Autowired
    private UserAuthService userAuthService;
    
    @Test
    public void emailVerificationTest() {
        userAuthService.sendEmailVerificationMail("sechan100@gmail.com");
    }
    
}