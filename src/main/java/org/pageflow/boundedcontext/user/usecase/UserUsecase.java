package org.pageflow.boundedcontext.user.usecase;

import org.pageflow.boundedcontext.user.dto.ApiRevealSignupForm;
import org.pageflow.boundedcontext.user.dto.SignupResult;

/**
 * @author : sechan
 */
public interface UserUsecase {
    // signup
    SignupResult signup(ApiRevealSignupForm apiForm);
    ApiRevealSignupForm getOauth2PresignupData(String username);

    // session
    void login(String username, String password);
}
