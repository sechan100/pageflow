package support.user;

import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.SignupCmd;
import org.pageflow.boundedcontext.user.UserUseCase;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.shared.ProviderType;

import java.util.Random;

/**
 * @author : sechan
 */
public class UserPersistencer {
    private final Random random;
    private final UserUseCase userUseCase;

    public UserPersistencer(int seed, UserUseCase userUseCase) {
        this.random = new Random(seed);
        this.userUseCase = userUseCase;
    }

    public UID registerOne() {
        UserDto.User user = userUseCase.signup(new SignupCmd(
            Username.from("tuser" + random.nextInt()),
            Password.encrypt("testpassword" + random.nextInt()),
            Email.from("testemail" + random.nextInt() + "@pageflow.org"),
            Penname.from("테스트사용자"),
            RoleType.ROLE_USER,
            ProviderType.NATIVE,
            ProfileImageUrl.from("/test")
        ));
        return UID.from(user.getUid());
    }
}