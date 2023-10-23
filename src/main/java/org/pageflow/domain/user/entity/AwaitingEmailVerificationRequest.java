package org.pageflow.domain.user.entity;



import lombok.Data;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * 이메일 인증을 위해 인증코드를 발급한 사용자의 정보를 임시로 저장하는 Redis Entity.
 */
@Data
@RedisHash(value = "AwaitingEmailVerification", timeToLive = 60 * 60)
public class AwaitingEmailVerificationRequest {
    
    @Id
    private String email;
    
    private String authenticationCode;
    
    private AccountDto account;
    
    private boolean isVerified = false;
    
    
    public AwaitingEmailVerificationRequest(AccountDto form, String authenticationCode){
        this(form, authenticationCode, false);
    }
    
    public AwaitingEmailVerificationRequest(AccountDto form, String authenticationCode, boolean isVerified){
        this.email = form.getEmail();
        this.authenticationCode = authenticationCode;
        this.account = form;
        this.isVerified = isVerified;
    }
    
    /* Spring Data framework spec: protected constructor */
    protected AwaitingEmailVerificationRequest(){}
}
