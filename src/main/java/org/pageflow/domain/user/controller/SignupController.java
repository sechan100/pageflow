package org.pageflow.domain.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.AlertType;
import org.pageflow.base.request.Rq;
import org.pageflow.base.validator.AccountDtoValidator;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.pageflow.domain.user.model.dto.AdditionalSignupAccountDto;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.pageflow.domain.user.model.dto.BasicSignupAccountDto;
import org.pageflow.domain.user.service.AccountService;
import org.pageflow.domain.user.service.AwaitingVerificationEmailService;
import org.pageflow.infra.util.Ut;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SignupController {
    
    private final Rq rq;
    private final AccountService accountService;
    private final AwaitingVerificationEmailService awaitingEmailVerifyingFormService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new AccountDtoValidator(accountService.getAccountRepository()));
    }
    
    
    @GetMapping("/signup")
    public String signupPage(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String email,
            Model model
    ) {
        
        /*
        * 이메일 인증 완료시 2단계 회원가입 페이지가 응답
        * */
        if(code != null && email != null){
            AwaitingEmailVerificationRequest awaitingEmailVerificationRequest = awaitingEmailVerifyingFormService.verify(email);
            
            // 2단계 회원가입 폼
            AdditionalSignupAccountDto form = new AdditionalSignupAccountDto(awaitingEmailVerificationRequest);
            model.addAttribute("registerForm", form);
            
            return "/user/account/verified_signup";
        }
        
        return "/user/account/signup";
    }
    
    
    /**
     * 이메일 인증용 메일 전송 요청
     * @param form 기본 회원가입 폼
     * @param model 뷰모델
     * @return 이메일 인증 대기 페이지
     */
    @PostMapping("/verify/email")
    public String verifyEmail(@Valid BasicSignupAccountDto form, Model model) {
        
        // 기존에 이메일 인증 요청을 보낸 기록이 있는지 확인
        boolean isExistInCache = awaitingEmailVerifyingFormService.existsById(form.getEmail());
        
        String authCode;
        
        /*
        * 기존에 발급한 유효한 인증코드가 남아있는 경우
        * */
        if(isExistInCache){
            
            // 기존 정보 가져옴
            authCode = awaitingEmailVerifyingFormService.findById(form.getEmail()).getAuthenticationCode();
            
            // 이메일로 인증코드 발급
            accountService.sendEmailVerifyingEmail(form.getEmail(), authCode);
          
        /*
        * 발급한 인증 코드가 없는 경우
        * */
        } else {
            
            // 인증코드 새로 발급
            authCode = Ut.generator.generateRandomString();
            
            // 이메일로 인증코드 발급
            accountService.sendEmailVerifyingEmail(form.getEmail(), authCode);
            
            // redis 캐쉬에 회원가입 정보 임시저장
            awaitingEmailVerifyingFormService.save(new AwaitingEmailVerificationRequest(form, authCode));
        }
        
        return "/user/account/email_verification_waiting";
    }
    
    
    @PostMapping("/signup")
    public String signup(@Valid AdditionalSignupAccountDto form){
        
        // 캐쉬에서 인증된 이메일 정보 가져오기
        AwaitingEmailVerificationRequest awaitingEmailVerificationRequest = awaitingEmailVerifyingFormService.findById(form.getEmail());
        AccountDto cachedSignupRequest = awaitingEmailVerificationRequest.getAccount();
        
        // 요청 받은 폼과 캐쉬에서 가져온 정보 대치
        if(!cachedSignupRequest.getEmail().equals(form.getEmail()) || !awaitingEmailVerificationRequest.isVerified()){
            throw new IllegalArgumentException(String.format("인증 정보가 일치하지 않습니다. 이메일 불일치인 경우 다음을 확인[기준: %s / 요청: %s] 인증되지 않은 경우 다음을 확인[isVerified: %b]", cachedSignupRequest.getEmail(), form.getEmail(), awaitingEmailVerificationRequest.isVerified())
            );
        }
        
        accountService.register(form);
        
        // 캐쉬 레코드 삭제
        awaitingEmailVerifyingFormService.delete(form.getEmail());
        
        return rq.alert(AlertType.SUCCESS, "회원가입이 완료되었습니다.", "/login");
    }
}
