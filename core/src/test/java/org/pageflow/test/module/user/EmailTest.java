package org.pageflow.test.module.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.test.e2e.PageflowIntegrationTest;
import org.pageflow.test.shared.DataCreator;
import org.pageflow.user.domain.entity.EmailVerificationRequest;
import org.pageflow.user.domain.service.AccountEmailService;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.pageflow.user.port.out.entity.EmailVerificationRequestPersistencePort;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class EmailTest {
  private final AccountEmailService accountEmailService;
  private final DataCreator dataCreator;
  private final AccountUseCase accountUseCase;
  private final AccountPersistencePort accountPersistencePort;
  private final EmailVerificationRequestPersistencePort verificationPersistencePort;

  @Test
  @DisplayName("인증 이메일 발송")
  void sendVerificationEmail() {
    UserDto user1 = dataCreator.createUser("user1");
    accountUseCase.changeEmail(user1.getUid(), "sechan100@gmail.com");
    accountPersistencePort.flush();
    accountEmailService.sendVerificationEmail(user1.getUid(), "/verify");
  }

  @Test
  @DisplayName("이메일 인증")
  void verifyEmail() {
    UserDto user1 = dataCreator.createUser("user1");
    // 인증받기 전에는 같은 이메일을 사용할 수 있음
    UserDto user2 = dataCreator.createUser("user2");
    accountUseCase.changeEmail(user2.getUid(), "user1@pageflow.org");
    accountPersistencePort.flush();

    // 강제 인증요청 생성
    EmailVerificationRequest evRequest = EmailVerificationRequest.of(user1.getUid(), "user1@pageflow.org");
    verificationPersistencePort.persist(evRequest);
    verificationPersistencePort.flush();

    // user1 이메일 인증
    accountEmailService.verify(EmailVerificationCmd.of(
      user1.getUid().getValue(),
      evRequest.getData().getEmail(),
      evRequest.getData().getAuthCode().toString()
    ));
    accountPersistencePort.flush();

    // 이제 user1@pageflow.org는 사용할 수 없음
    Assertions.assertThrows(
      ProcessResultException.class,
      () -> accountEmailService.sendVerificationEmail(user2.getUid(), "/verify"),
      "이미 사용중인 이메일은 사용할 수 없는데 있네..?"
    );
    accountPersistencePort.flush();
  }
}
