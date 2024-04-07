package org.pageflow.boundedcontext.user.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.service.AuthService;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : sechan
 */
@Controller
@RequiredArgsConstructor
public class EmailVerifyView {
    
    private final AuthService authService;
    
    /**
     * @param email 인증대상 이메일
     * @param code  인증코드(UUID)
     */
    @GetMapping(AuthService.EMAIL_VERIFICATION_URI)
    public String verifyEmail(TSID UID, String email, String code) {
        authService.verifyEmail(UID, email, code);
        
        return "/user/email-verify-success";
    }
    
    @GetMapping("/send/email")
    public String sendEmailVerification(TSID UID, String email) {
        authService.sendEmailVerificationMail(UID, email);
        
        return "/user/email-verify-success";
    }
}
