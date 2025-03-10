package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.pageflow.user.adapter.in.req.EmailReq;
import org.pageflow.user.adapter.in.req.PennameReq;
import org.pageflow.user.adapter.in.res.AccountRes;
import org.pageflow.user.adapter.in.res.ProfileRes;
import org.pageflow.user.dto.AccountDto;
import org.pageflow.user.dto.ProfileDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.ProfileUseCase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "user-update", description = "다양한 사용자 필드 변경")
public class UserUpdateWebAdapter {
  private final RequestContext rqrxt;
  private final AccountUseCase accountUsecase;
  private final ProfileUseCase profileUseCase;


  @PostMapping("/email")
  public AccountRes changeEmail(@RequestBody EmailReq req) {
    UID uid = rqrxt.getUid();
    AccountDto result = accountUsecase.changeEmail(uid, req.getEmail());
    return AccountRes.from(result);
  }


  @PostMapping("/penname")
  public ProfileRes changePenname(@RequestBody PennameReq req) {
    UID uid = rqrxt.getUid();
    ProfileDto result = profileUseCase.changePenname(uid, req.getPenname());
    return ProfileRes.from(result);
  }


  @PostMapping("/profile-image")
  @Operation(summary = "프로필 이미지 변경")
  public ProfileRes changeProfileImage(
    @RequestPart(required = false)
    MultipartFile file,

    @Parameter(description = "해당 플래그를 true로하면 기존 프로필 이미지를 삭제하고 기본 값으로 되돌립니다.")
    @RequestParam(defaultValue = "false")
    Boolean delete
  ) {
    UID uid = rqrxt.getUid();
    ProfileDto result = null;
    if(delete){
      result = profileUseCase.deleteProfileImage(uid);
    } else {
      result = profileUseCase.changeProfileImage(uid, file);
    }

    return ProfileRes.from(result);
  }
}
