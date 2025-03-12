package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "email verification", description = "이메일 인증")
public class EmailVerificationWebAdapter {
  private final RequestContext rqrxt;
  private final AccountUseCase accountUsecase;

  private static final String VERIFY_URI = "/email/verify";

  @PostMapping("/user/email/verification")
  @Operation(summary = "이메일 인증 메일 요청")
  public void requestEmailVerification() {
    UID uid = rqrxt.getUid();
    Result result = accountUsecase.sendEmailVerificationMail(uid, VERIFY_URI);
    if(result.isFailure()) {
      throw new ProcessResultException(result);
    }
  }

  @GetMapping(VERIFY_URI)
  @Operation(summary = "이메일 인증")
  public void verifyEmail(
    @RequestParam UID uid,
    @RequestParam String email,
    @RequestParam UUID authCode
  ) {
    EmailVerificationCmd cmd = new EmailVerificationCmd(
      uid,
      email,
      authCode
    );
    accountUsecase.verifyEmail(cmd);
  }
}
