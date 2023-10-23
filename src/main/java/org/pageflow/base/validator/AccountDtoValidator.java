package org.pageflow.base.validator;


import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.pageflow.domain.user.model.dto.VerifyRequestRegisterForm;
import org.pageflow.domain.user.repository.AccountRepository;
import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 회원가입시 입력된 정보의 유효성 검사
 * @see org.pageflow.domain.user.constants.UserSignupPolicy 유효성 판단기준 정의서
 */
@RequiredArgsConstructor
public class AccountDtoValidator implements Validator {
    
    private final AccountRepository accountRepository;
    
    
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return AccountDto.class.isAssignableFrom(clazz);
    }
    
    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        AccountDto account = (AccountDto) target;
        
        // username 중복검사
        if(accountRepository.existsByUsername(account.getUsername())) {
            errors.rejectValue("username", "username", UserSignupPolicy.ViolationType.USERNAME_DUPLICATION);
        }
        
        // 사용할 수 없는 username
        if(UserSignupPolicy.Constraints.getInvalidUsernames().contains(account.getUsername())) {
            errors.rejectValue("username", "username", UserSignupPolicy.ViolationType.INVALID_USERNAME);
        }
        
        // email 중복검사 (provider가 다르다면 같은 이메일을 가질 수 있다.)
        if(accountRepository.existsByEmailAndProvider(account.getEmail(), account.getProvider())) {
            errors.rejectValue("email", "email", UserSignupPolicy.ViolationType.EMAIL_DUPLICATION);
        }
        
        // VerifyRequestRegisterForm타입인 경우, passwordConfirm 일치검사
        if(account instanceof VerifyRequestRegisterForm verifyRequestRegisterForm) {
            if(!verifyRequestRegisterForm.getPassword().equals(verifyRequestRegisterForm.getPasswordConfirm())) {
                errors.rejectValue("passwordConfirm", "passwordConfirm", UserSignupPolicy.ViolationType.INVALID_PASSWORD_CONFIRM);
            }
        }
        
    }
}
