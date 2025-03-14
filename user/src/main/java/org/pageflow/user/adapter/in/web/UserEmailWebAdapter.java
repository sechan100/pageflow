package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.adapter.in.req.EmailReq;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.EmailVerificationCmd;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "user email", description = "사용자 이메일 인증, 변경등")
public class UserEmailWebAdapter {
  private final RequestContext rqrxt;
  private final AccountUseCase accountUsecase;

  private static final String VERIFY_URI = "/email/verify";

  @PostMapping("/user/email/verification")
  @Operation(summary = "이메일 인증 메일 요청")
  public Result requestEmailVerification(@RequestBody EmailReq req) {
    UID uid = rqrxt.getUid();
    return accountUsecase.sendVerificationMail(uid, req.getEmail(), VERIFY_URI);
  }

  @GetMapping(VERIFY_URI)
  @Operation(summary = "이메일 인증 및 변경")
  public Result verifyEmail(
    @RequestParam UID uid,
    @RequestParam String email,
    @RequestParam UUID authCode
  ) {
    EmailVerificationCmd cmd = new EmailVerificationCmd(
      uid,
      email,
      authCode
    );
    return accountUsecase.verifyEmail(cmd);
  }

}
