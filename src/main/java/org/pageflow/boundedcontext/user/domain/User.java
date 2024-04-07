package org.pageflow.boundedcontext.user.domain;

import org.pageflow.boundedcontext.user.command.SignupCmd;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.ApiRevealSignupForm;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.shared.infra.domain.AggregateRoot;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */

public class User extends AggregateRoot<TSID> {

    private String username;

    public User(TSID uid){
        super(uid);
    }

    public static User signup(ApiRevealSignupForm apiRevealSignupForm, ProviderType provider, RoleType role){
        TSID uid = TSID.Factory.getTsid();
        User user = new User(uid);
        SignupForm form = SignupForm.from(
            apiRevealSignupForm,
            uid,
            provider,
            role
        );
        // 이벤트 발행
        user.raiseEvent(new SignupCmd(user, form));
        return user;
    }


}
