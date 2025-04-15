package org.pageflow.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.adapter.in.req.ChangePasswordReq;
import org.pageflow.user.adapter.in.req.ProfileUpdateReq;
import org.pageflow.user.adapter.in.res.UserRes;
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
public class UserUpdateWebAdapter {
  private final RequestContext rqrxt;
  private final UserUseCase userUseCase;
  private final AccountUseCase accountUsecase;
  private final ProfileUseCase profileUseCase;


  @PostMapping("/profile")
  @Transactional
  @Operation(summary = "프로필 설정 변경")
  public Result<UserRes> changeProfile(
    @RequestPart("form")
    ProfileUpdateReq req,

    @RequestPart(name = "profileImage", required = false)
    MultipartFile profileImage,

    @Parameter(description = "해당 플래그를 켜면, 기존 프로필 이미지를 삭제하고 기본 값으로 되돌립니다.")
    @RequestParam(defaultValue = "false")
    boolean toDefaultProfileImage
  ) {
    UID uid = rqrxt.getUid();
    if(req.getPenname() != null) {
      Result<UserDto> changePennameResult = profileUseCase.changePenname(uid, req.getPenname());
      if(changePennameResult.isFailure()) {
        return (Result) changePennameResult;
      }
    }
    if(profileImage != null) {
      Result<UserDto> changeProfileImageResult = profileUseCase.changeProfileImage(uid, profileImage);
      if(changeProfileImageResult.isFailure()) {
        return (Result) changeProfileImageResult;
      }
    }
    if(toDefaultProfileImage) {
      Result deleteProfileImageResult = profileUseCase.deleteProfileImage(uid);
      if(deleteProfileImageResult.isFailure()) {
        return (Result) deleteProfileImageResult;
      }
    }
    Optional<UserDto> userDtoOpt = userUseCase.queryUser(uid);
    assert userDtoOpt.isPresent();
    return Result.SUCCESS(new UserRes(userDtoOpt.get()));
  }


  @PostMapping("/password")
  @Operation(summary = "비밀번호 변경")
  public Result changePassword(@RequestBody ChangePasswordReq req) {
    UID uid = rqrxt.getUid();
    Result<UserDto> result = accountUsecase.changePassword(uid, req.getCurrentPassword(), req.getNewPassword());
    if(result.isFailure()) return (Result) result;
    return Result.SUCCESS();
  }
}
