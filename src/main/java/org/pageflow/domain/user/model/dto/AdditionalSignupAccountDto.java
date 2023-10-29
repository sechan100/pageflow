package org.pageflow.domain.user.model.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdditionalSignupAccountDto extends DefaultAccountDto {
    
    // profileImg, profileImgUrl 둘 중 하나만 있으면 된다.
    private MultipartFile profileImg;
    private String profileImgUrl;
    
    @NotEmpty
    private String nickname;
    
    
    
    public AdditionalSignupAccountDto(){
         provider = ProviderType.NATIVE;
    }
    
    public AdditionalSignupAccountDto(AccountDto form){
        super.username = form.getUsername();
        super.password = form.getPassword();
        super.email = form.getEmail();
        super.provider = form.getProvider();
    }
    public AdditionalSignupAccountDto(AwaitingEmailVerificationRequest form){
        super.email = form.getEmail();
        super.username = form.getAccountDto().getUsername();
        super.password = form.getAccountDto().getPassword();
        super.provider = form.getAccountDto().getProvider();
    }
    
}
