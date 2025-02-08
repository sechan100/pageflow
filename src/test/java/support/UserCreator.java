package support;

import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author : sechan
 */
@Component
public class UserCreator {
  @Autowired
  private UserUseCase userUseCase;

  private final Random random = new Random(1);

  public UID create() {
    return create(null);
  }

  public UID create(String username) {
    UserDto.User user = userUseCase.signup(new SignupCmd(
      username!=null ? username:"tuser" + random.nextInt(),
      "tuser" + random.nextInt(),
      "testemail" + random.nextInt() + "@pageflow.org",
      "테스트사용자" + random.nextInt(),
      RoleType.ROLE_USER,
      ProviderType.NATIVE,
      "/test"
    ));
    return UID.from(user.getId());
  }
}
