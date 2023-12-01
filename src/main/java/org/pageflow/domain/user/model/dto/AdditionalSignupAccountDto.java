package org.pageflow.domain.user.model.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class AdditionalSignupAccountDto extends DefaultAccountDto {
    
    // 2개 다 설정하면 profileImg를 우선적으로 사용.
    private MultipartFile profileImg;
    private String profileImgUrl;

    @NotEmpty
    private String nickname;


    public AdditionalSignupAccountDto() {
        provider = ProviderType.NATIVE;
    }

    public AdditionalSignupAccountDto(AccountDto form) {
        super.username = form.getUsername();
        super.password = form.getPassword();
        super.email = form.getEmail();
        super.provider = form.getProvider();
    }

    public AdditionalSignupAccountDto(AwaitingEmailVerificationRequest form) {
        super.email = form.getEmail();
        super.username = form.getAccountDto().getUsername();
        super.password = form.getAccountDto().getPassword();
        super.provider = form.getAccountDto().getProvider();

        // OAuth2를 통한 회원가입인 경우, 추가적인 프로필 정보의 기본 베이스가 존재.
        if (!provider.equals(ProviderType.NATIVE)) {
            profileImgUrl = form.getOAuth2AdditionalProfileData().getProfileImgUrl();
            nickname = form.getOAuth2AdditionalProfileData().getNickname();
        }
    }

}
