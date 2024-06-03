package org.pageflow.global.dev.user;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.initialize.RuntimeInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
@Profile("dev")
public class RiUserCreator implements RuntimeInitializer {
    @Value("${dev.data.user.active}")
    private boolean active;
    @Value("${dev.data.user.count}")
    private int count;
    @Value("${dev.data.user.random-seed}")
    private int seed;

    private final UserUseCase userUseCase;


    @Override
    public void initialize() {
        for(int i = 1; i < count + 1; i++){
            UserDto.User user = userUseCase.signup(new SignupCmd(
                "tuser" + i,
                "tuser" + i,
                "testemail" + i + "@pageflow.org",
                "테스트사용자" + i,
                RoleType.ROLE_USER,
                ProviderType.NATIVE,
                "/test"
            ));
        }
    }

    @Override
    public boolean isActivated() {
        return active;
    }
}
