package org.pageflow.global.dev;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.initialize.RuntimeInitializer;
import org.pageflow.shared.type.TSID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * dev 모드에서 앱이 시작되면 더미 데이터를 Runtime Initialization 시간에 생성한다.
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
@Profile("dev")
public class RiDataCreator implements RuntimeInitializer {
    @Value("${dev.data.enabled}")
    private boolean enabled;
    @Value("${dev.data.random-seed}")
    private int seed;
    @Value("${dev.data.user.count}")
    private int userCount;

    private final Set<TSID> userIds;
    private final UserUseCase userUseCase;
    private final BookCreator bookCreator;

    @Override
    public void initialize() {
        if(!enabled){
            return;
        }
        // 사용자 생성
        for(int i = 1; i < userCount + 1; i++){
            var user = userUseCase.signup(new SignupCmd(
                "tuser" + i,
                "tuser" + i,
                "testemail" + i + "@pageflow.org",
                "테스트사용자" + i,
                RoleType.ROLE_USER,
                ProviderType.NATIVE,
                "/test"
            ));
            userIds.add(user.getId());
        }

        // 책 생성
        bookCreator.create(userIds);
    }
}
