package org.pageflow.boundedcontext.user.dto;

import lombok.Data;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.shared.type.TSID;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Data
public class SignupForm {
    private final TSID id;
    private final ProviderType provider;
    private final RoleType roleType;
    private final String username;
    private final String password;
    private final String email;
    private final String penname;
    @Nullable
    private final String profileImgUrl;

    public static SignupForm from(ApiRevealSignupForm form, TSID uid, ProviderType provider, RoleType roleType) {
        return new SignupForm(
            uid,
            provider,
            roleType,
            form.getUsername(),
            form.getPassword(),
            form.getEmail(),
            form.getPenname(),
            form.getProfileImgUrl()
        );
    }
}
