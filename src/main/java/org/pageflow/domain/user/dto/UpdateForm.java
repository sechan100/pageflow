package org.pageflow.domain.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.springframework.lang.Nullable;


@Getter
@Setter
@Builder
public class UpdateForm {
    
    @NotBlank
    @Pattern(regexp = UserSignupPolicy.USERNAME_REGEX, message = UserSignupPolicy.USERNAME_REGEX_DISCRIPTION)
    private String username;
    
    @NotBlank
    @Pattern(regexp = UserSignupPolicy.PASSWORD_REGEX, message = UserSignupPolicy.PASSWORD_REGEX_DISCRIPTION)
    private String password;
    
    @NotBlank
    private String passwordConfirm;
    
    @Email
    private String email;
    
    @NotBlank
    @Pattern(regexp = UserSignupPolicy.PENNAME_REGEX, message = UserSignupPolicy.PENNAME_REGEX_DISCRIPTION)
    private String penname;
    
    @Nullable
    private String profileImgUrl;
    
}