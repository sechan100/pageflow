package org.pageflow.boundedcontext.user.model.dto;


import io.sseg.boundedcontext.user.model.oauth.ProviderUser;
import io.sseg.base.security.constants.ProviderType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDetailsRegisterForm extends DefaultAccountDto {
    
    @NotEmpty
    private String nickname;
    
    
    
    public AccountDetailsRegisterForm(){
         provider = ProviderType.NATIVE;
    }
    
    public AccountDetailsRegisterForm(ProviderUser form){
        super.username = form.getUsername();
        super.password = form.getPassword();
        super.email = form.getEmail();
        super.provider = form.getProvider();
    }

    
    public AccountDetailsRegisterForm(AccountDto form){
        super.username = form.getUsername();
        super.password = form.getPassword();
        super.email = form.getEmail();
        super.provider = form.getProvider();
    }
    
    public AccountDetailsRegisterForm(AwaitingEmailVerifyingRedisEntity form){
        super.username = form.getUsername();
        super.password = form.getPassword();
        super.email = form.getEmail();
        super.provider = form.getProvider();
    }
    
}
