package org.pageflow.test.module.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.test.e2e.PageflowIntegrationTest;
import org.pageflow.test.shared.DataCreator;
import org.pageflow.user.domain.service.EmailService;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.out.entity.AccountPersistencePort;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class EmailTest {
  private final EmailService emailService;
  private final DataCreator dataCreator;
  private final AccountUseCase accountUseCase;
  private final AccountPersistencePort port;

  @Test
  @DisplayName("인증 이메일 발송")
  void sendVerificationEmail() {
    UserDto user1 = dataCreator.createUser("user1");
    accountUseCase.changeEmail(user1.getUid(), "sechan100@gmail.com");
    port.flush();
    emailService.sendVerificationEmail(user1.getUid(), "/verify");
  }
}
