package org.pageflow.domain.user.model.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.pageflow.domain.user.model.oauth.ResourceOwner;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDetailsRegisterForm extends DefaultAccountDto {
    
    @NotEmpty
    private String nickname;
    
    
    
    public AccountDetailsRegisterForm(){
         provider = ProviderType.NATIVE;
    }
    
    public AccountDetailsRegisterForm(ResourceOwner form){
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
    
    public AccountDetailsRegisterForm(AwaitingEmailVerificationRequest form){
        super.email = form.getEmail();
        super.username = form.getAccount().getUsername();
        super.password = form.getAccount().getPassword();
        super.provider = form.getAccount().getProvider();
    }
    
}
