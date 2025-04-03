package org.pageflow.test.module.user;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.dto.token.AuthTokens;
import org.pageflow.user.port.in.IssueSessionCmd;
import org.pageflow.user.port.in.SessionUseCase;
import org.pageflow.user.port.out.LoadAccountPort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class TestAccessTokenIssuer {
  private final SessionUseCase sessionUseCase;
  private final LoadAccountPort loadAccountPort;
  private final ApplicationProperties applicationProperties;

  /**
   * 발행과 동시에 만료되는 토큰을 발행합니다.
   * 해당 세션은 유지되지 않습니다.(즉시 로그아웃)
   */
  public String issueImmediatelyExpireToken(String username) {
    Account account = loadAccountPort.load(username).get();
    var cmd = new IssueSessionCmd(account.getUid());
    try {
      int originalExpireMinutes = applicationProperties.auth.accessTokenExpireMinutes;
      // reflection으로 application properties 일시적으로 조작
      Field field = ApplicationProperties.Auth.class.getDeclaredField("accessTokenExpireMinutes");
      field.setAccessible(true);
      field.setInt(applicationProperties.auth, 0);

      // 조작한 accessTokenExpireMinutes로 세션 발급
      AuthTokens result = sessionUseCase.issueSession(cmd);
      sessionUseCase.logout(result.getRefreshToken().getSessionId());

      // reflection으로 application properties 원복
      field.setInt(applicationProperties.auth, originalExpireMinutes);
      field.setAccessible(false);
      return result.getAccessToken().getCompact();

    } catch(NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
