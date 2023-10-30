package org.pageflow.domain.user.model.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pageflow.domain.user.constants.ProviderType;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasicSignupAccountDto extends DefaultAccountDto {
    
    @NotEmpty(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;
    
    public BasicSignupAccountDto() {
        super.provider = ProviderType.NATIVE;
    }
    
    
    public BasicSignupAccountDto(AccountDto accountDto) {
        super.username = accountDto.getUsername();
        super.password = accountDto.getPassword();
        super.email = accountDto.getEmail();
        super.provider = accountDto.getProvider();
    }
}
