package org.pageflow.user.port.in.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.PropsAware;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldValidationResult;
import org.pageflow.common.validation.ImageUrlValidator;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.FileUploadCmd;
import org.pageflow.file.service.FileService;
import org.pageflow.file.shared.FileType;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.ProfileUseCase;
import org.pageflow.user.port.out.entity.UserPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {
  private static final String WEB_BASE_URL = PropsAware.use().file.public_.webBaseUrl;
  private static final String DEFAULT_PROFILE_IMAGE_URL = PropsAware.use().user.defaultProfileImageUrl;

  private final UserPersistencePort userPersistencePort;
  private final PennameValidator pennameValidator;
  private final FileService fileService;
  private final ImageUrlValidator imageUrlValidator;


  /**
   * @param uid
   * @param file
   * @code FIELD_VALIDATION_ERROR: file 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: 파일 업로드에 실패한 경우
   */
  @Override
  public Result<UserDto> changeProfileImage(UID uid, MultipartFile file) {
    // 기존 사용자의 프로필 이미지 삭제 ============================
    User user = userPersistencePort.findById(uid.getValue()).get();
    String oldUrl = user.getProfileImageUrl();
    if(imageUrlValidator.isInternalUrl(oldUrl)) {
      FilePath path = FilePath.fromWebUrl(oldUrl);
      fileService.delete(path);
    }

    // 새 이미지 업로드
    FileUploadCmd cmd = FileUploadCmd.createCmd(
      file,
      uid.getValue().toString(),
      FileType.USER_PROFILE_IMAGE
    );
    FilePath newProfileImagePath = fileService.upload(cmd);

    // user 엔티티 변경 =============================
    String newWebUrl = newProfileImagePath.getWebUrl();
    user.changeProfileImageUrl(newWebUrl);
    return Result.ok(new UserDto(user));
  }

  /**
   * @param uid
   * @return
   * @code FAIL_TO_DELETE_FILE: 파일 삭제에 실패한 경우
   */
  @Override
  public Result<UserDto> deleteProfileImage(UID uid) {
    // 내부에 저장된 이미지인 경우, 기존 이미지를 삭제 =========================
    User user = userPersistencePort.findById(uid.getValue()).orElseThrow();
    String oldUrl = user.getProfileImageUrl();
    if(imageUrlValidator.isInternalUrl(oldUrl)) {
      FilePath path = FilePath.fromWebUrl(oldUrl);
      fileService.delete(path);
    }

    // 기본 이미지로 변경 ===========
    user.changeProfileImageUrl(DEFAULT_PROFILE_IMAGE_URL);
    return Result.ok(new UserDto(user));
  }

  /**
   * @param uid
   * @param penname
   * @return
   * @code FIELD_VALIDATION_ERROR: penname 데이터가 올바르지 않은 경우
   */
  @Override
  public Result<UserDto> changePenname(UID uid, String penname) {
    FieldValidationResult validationResult = pennameValidator.validate(penname);
    if(!validationResult.isValid()) {
      return Result.unit(CommonCode.FIELD_VALIDATION_ERROR, validationResult);
    }

    User user = userPersistencePort.findById(uid.getValue()).get();
    user.changePenname(penname);
    return Result.ok(new UserDto(user));
  }

}
