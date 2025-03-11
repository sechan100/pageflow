package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.adapter.in.req.ProfileUpdateReq;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.AccountUseCase;
import org.pageflow.user.port.in.ProfileUseCase;
import org.pageflow.user.port.in.UserUseCase;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "user-update", description = "다양한 사용자 필드 변경")
public class UserProfileUpdateWebAdapter {
  private final RequestContext rqrxt;
  private final UserUseCase userUseCase;
  private final AccountUseCase accountUsecase;
  private final ProfileUseCase profileUseCase;


  @PostMapping("/profile")
  @Transactional
  @Operation(summary = "프로필 설정 변경")
  public Result<UserDto> changeProfile(
    @RequestPart("form")
    ProfileUpdateReq req,
    @RequestPart(required = false)
    MultipartFile file,
    @Parameter(description = "해당 플래그를 true로하면 기존 프로필 이미지를 삭제하고 기본 값으로 되돌립니다.")
    @RequestParam(defaultValue = "false") boolean toDefaultProfileImage
  ) {
    UID uid = rqrxt.getUid();
    if(req.getEmail() != null) {
      accountUsecase.changeEmail(uid, req.getEmail());
    }
    if(req.getPenname() != null) {
      profileUseCase.changePenname(uid, req.getPenname());
    }
    if(file != null) {
      profileUseCase.changeProfileImage(uid, file);
    }
    if(toDefaultProfileImage) {
      profileUseCase.deleteProfileImage(uid);
    }
    Optional<UserDto> userDtoOpt = userUseCase.queryUser(uid);
    assert userDtoOpt.isPresent();
    return Result.success(userDtoOpt.get());
  }
}
