package org.pageflow.user.port.in;

import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public interface ProfileUseCase {

  Result<UserDto> changeProfileImage(UID uid, MultipartFile file);

  /**
   * 프로필 이미지를 삭제하고 기본 이미지로 변경한다.
   *
   * @param uid
   * @return
   */
  Result<UserDto> deleteProfileImage(UID uid);

  Result<UserDto> changePenname(UID uid, String penname);
}
