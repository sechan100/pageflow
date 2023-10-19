package org.pageflow.boundedcontext.user.model.dto;



import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "awaitingVerify", timeToLive = 60 * 60)
public class AwaitingEmailVerifyingRedisEntity {
    
    @Id
    private String email;
    
    private String provider;
    
    private String username;
    
    private String password;
    
    private String authenticationCode;
    
    public AwaitingEmailVerifyingRedisEntity(AccountDto form, String authenticationCode){
        this.email = form.getEmail();
        this.username = form.getUsername();
        this.password = form.getPassword();
        this.authenticationCode = authenticationCode;
        this.provider = form.getProvider();
    }
    
    protected AwaitingEmailVerifyingRedisEntity(){}
}
