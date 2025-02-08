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
 *
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
@Profile("dev")
public class RiDataCreator implements RuntimeInitializer {
  /**
   * 데이터를 생성할지의 여부를 결정하는 값
   * - enable: 생성함
   * - disable: 생성하지 않음
   * - according-to-ddl: jpa의 ddl-auto 설정이 create 또는 create-drop일 때만 생성함
   */
  // enable, disable, according-to-ddl
  @Value("${dev.data.enabled}")
  private String enabled;
  @Value("${spring.jpa.hibernate.ddl-auto}")
  private String ddlAuto;

  @Value("${dev.data.random-seed}")
  private int seed;

  @Value("${dev.data.user.count}")
  private int userCount;

  private final Set<TSID> userIds;
  private final UserUseCase userUseCase;
  private final BookCreator bookCreator;

  @Override
  public void initialize() {
    boolean isEnabled;
    switch(enabled){
      case "enable":
        isEnabled = true;
        break;
      case "disable":
        isEnabled = false;
        break;
      case "according-to-ddl":
        isEnabled = "create".equals(ddlAuto) || "create-drop".equals(ddlAuto);
        break;
      default:
        throw new IllegalArgumentException("dev.data.enabled 값이 올바르지 않습니다.");
    }
    if(!isEnabled) return;

    // 사용자 생성
    for(int i = 1; i < userCount + 1; i++){
      var user = userUseCase.signup(new SignupCmd(
        "user" + i,
        "user" + i,
        "user" + i + "@pageflow.org",
        "테스트사용자" + i,
        RoleType.ROLE_USER,
        ProviderType.NATIVE,
        "/test.jpg"
      ));
      userIds.add(user.getId());
    }

    // 책 생성
    bookCreator.create(userIds);
  }
}
