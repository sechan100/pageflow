package org.pageflow.user.port.in.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.PropsAware;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.FieldValidator;
import org.pageflow.common.validation.ImageUrlValidator;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.ImageFileUploadCmd;
import org.pageflow.file.service.FileService;
import org.pageflow.user.domain.entity.Profile;
import org.pageflow.user.dto.ProfileDto;
import org.pageflow.user.port.in.ProfileUseCase;
import org.pageflow.user.port.out.entity.ProfilePersistencePort;
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

  private final PennameValidator pennameValidator;
  private final ProfilePersistencePort profilePersistencePort;
  private final FileService fileService;
  private final ImageUrlValidator imageUrlValidator;


  @Override
  public ProfileDto changeProfileImage(UID uid, MultipartFile file) {
    FieldValidator<MultipartFile> validator = new FieldValidator<>("file", file)
      .rule(v -> !v.isEmpty(), FieldReason.EMPTY, "첨부된 파일이 없습니다.")
      .rule(v -> v.getOriginalFilename() != null, FieldReason.INVALID_VALUE, "파일 이름이 없습니다.");
    var validation = validator.validate();
    validation.throwIfInvalid();

    Profile profile = profilePersistencePort.findById(uid.getValue()).orElseThrow();
    String oldUrl = profile.getProfileImageUrl();

    // 내부에 저장된 이미지인 경우, 기존 이미지를 삭제
    if(imageUrlValidator.isInternalUrl(oldUrl)) {
      fileService.delete(oldUrl);
    }

    // 새 이미지 업로드
    ImageFileUploadCmd cmd = new ImageFileUploadCmd(
      uid.getValue().toString(),
      FileTypeeee.USER.PROFILE_IMAGE,
      file
    );
    FilePath path = fileService.upload(cmd);
    // 변경
    profile.changeProfileImageUrl(path.getWebUrl());
    return ProfileDto.from(profile);
  }

  @Override
  public ProfileDto deleteProfileImage(UID uid) {
    Profile user = profilePersistencePort.findById(uid.getValue()).orElseThrow();
    String oldUrl = user.getProfileImageUrl();

    // 내부에 저장된 이미지인 경우, 기존 이미지를 삭제
    if(imageUrlValidator.isInternalUrl(oldUrl)) {
      fileService.delete(oldUrl);
    }

    // 기본 이미지로 변경
    user.changeProfileImageUrl(DEFAULT_PROFILE_IMAGE_URL);
    return ProfileDto.from(user);
  }

  @Override
  public ProfileDto changePenname(UID uid, String penname) {
    var validation = pennameValidator.validate(penname);
    if(!validation.isValid()) {
      throw new FieldValidationException(validation);
    }

    Profile profile = load(uid);
    profile.changePenname(penname);
    return ProfileDto.from(profile);
  }

  private Profile load(UID uid) {
    return profilePersistencePort.findById(uid.getValue()).orElseThrow();
  }
}
