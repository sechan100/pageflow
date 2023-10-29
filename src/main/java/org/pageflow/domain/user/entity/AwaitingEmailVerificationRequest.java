package org.pageflow.domain.user.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.pageflow.domain.user.model.dto.AccountDto;
import org.pageflow.domain.user.model.dto.OAuth2AdditionalProfileData;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * 이메일 인증을 위해 인증코드를 발급한 사용자의 정보를 임시로 저장하는 Redis Entity.
 */
@Data
@Builder
@AllArgsConstructor
@RedisHash(value = "AwaitingEmailVerification", timeToLive = 60 * 60)
public class AwaitingEmailVerificationRequest {
    
    @Id
    private String email;
    
    private String authenticationCode;
    
    private AccountDto accountDto;
    
    private OAuth2AdditionalProfileData oAuth2AdditionalProfileData; // OAuth2 회원가입인 경우에만 값이 존재하거나 유효.
    
    private boolean isVerified = false;
    
    
    
    public AwaitingEmailVerificationRequest(AccountDto accountDto, String authenticationCode, boolean isVerified){
        this.email = accountDto.getEmail();
        this.authenticationCode = authenticationCode;
        this.accountDto = accountDto;
        this.isVerified = isVerified;
    }
    
    
    
    /* Spring Data framework spec: protected constructor */
    protected AwaitingEmailVerificationRequest(){}
}
