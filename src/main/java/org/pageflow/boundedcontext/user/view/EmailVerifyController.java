package org.pageflow.boundedcontext.user.view;

import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : sechan
 */
@Controller
@RequiredArgsConstructor
public class EmailVerifyController {
    
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
