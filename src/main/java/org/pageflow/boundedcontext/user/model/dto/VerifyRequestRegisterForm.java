package org.pageflow.boundedcontext.user.model.dto;


import io.sseg.base.security.constants.ProviderType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VerifyRequestRegisterForm extends DefaultAccountDto {
    
    @NotEmpty(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;
    
    public VerifyRequestRegisterForm() {
        super.provider = ProviderType.NATIVE;
    }
    
}
