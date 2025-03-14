package org.pageflow.test.module.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.test.e2e.PageflowIntegrationTest;
import org.pageflow.test.shared.DataCreator;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.domain.entity.EmailVerificationRequest;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.pageflow.user.port.out.entity.EmailVerificationRequestPersistencePort;

import java.lang.reflect.Field;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class EmailTest {
  private final DataCreator dataCreator;
  private final AccountUseCase accountUseCase;
  private final AccountPersistencePort accountPersistencePort;
  private final EmailVerificationRequestPersistencePort verificationPersistencePort;

  @Test
  @DisplayName("인증 이메일 발송")
  void sendVerificationEmail() {
    UserDto user1 = dataCreator.createUser("user1");
    _forceChangeEmail(user1.getUid(), "sechan100@gmail.com");
    Result result = accountUseCase.sendVerificationMail(user1.getUid(), "/verify");
    Assertions.assertEquals(CommonCode.SUCCESS, result.getCode());
  }

  @Test
  @DisplayName("이메일 인증")
  void verifyEmail() {
    UserDto user1 = dataCreator.createUser("user1");
    UserDto user2 = dataCreator.createUser("user2");
    // user1의 강제 이메일 인증
    _forceVerifyEmail(user1);

    // user1의 이메일이 이미 인증된 상태인데 다시 인증요청을 보내면 실패
    Result result = accountUseCase.sendVerificationMail(user1.getUid(), "/verify");
    Assertions.assertEquals(UserCode.EMAIL_ALREADY_VERIFIED, result.getCode());

    // user2의 email을 user1과 동일하게 변경
    // 아직 user1이 이메일을 인증받지 않았기 때문에 변경 가능
    _forceChangeEmail(user2.getUid(), user1.getEmail());

    // 이제 user1@pageflow.org는 사용할 수 없음
    // user2가 user1@pageflow.org로 이메일 인증요청을 보내면 실패.
    result = accountUseCase.sendVerificationMail(user2.getUid(), "/verify");
    Assertions.assertEquals(CommonCode.FIELD_VALIDATION_ERROR, result.getCode());

    // user2도 원래 user2의 이메일로 변경하고 인증
    _forceChangeEmail(user2.getUid(), user2.getEmail());
    _forceVerifyEmail(user2);

    // user2는 이미 인증된 이메일을 가지고있음. 이 때 user1@pageflow.org로 변경 및 인증 요청을 보내지만 실패.(이미 사용중)
    result = accountUseCase.sendVerificationMailForChangeEmail(user2.getUid(), user1.getEmail(), "/verify");
    Assertions.assertEquals(CommonCode.FIELD_VALIDATION_ERROR, result.getCode());
  }

  private void _forceChangeEmail(UID uid, String email) {
    Account a = accountPersistencePort.findById(uid.getValue()).get();
    try {
      Field emailField = Account.class.getDeclaredField("email");
      emailField.setAccessible(true);
      emailField.set(a, email);
    } catch(Exception e){
      throw new RuntimeException(e);
    }
    accountPersistencePort.flush();
  }

  private void _forceVerifyEmail(UserDto user) {
    EmailVerificationRequest evRequest = EmailVerificationRequest.of(user.getUid(), user.getEmail());
    verificationPersistencePort.persist(evRequest);
    verificationPersistencePort.flush();
    accountUseCase.verifyEmail(new EmailVerificationCmd(
      user.getUid(),
      evRequest.getData().getEmail(),
      evRequest.getData().getAuthCode()
    ));
    accountPersistencePort.flush();
  }
}
