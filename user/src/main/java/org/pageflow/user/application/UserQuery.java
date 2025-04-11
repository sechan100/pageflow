package org.pageflow.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.dto.SessionUserDto;
import org.pageflow.user.port.out.LoadSessionUserPort;
import org.pageflow.user.port.out.entity.UserPersistencePort;
import org.springframework.stereotype.Service;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserQuery implements LoadSessionUserPort {
  private final UserPersistencePort userPersistencePort;

  @Override
  public SessionUserDto load(UID uid) {
    User user = userPersistencePort.findById(uid.getValue()).get();
    SessionUserDto dto = SessionUserDto.builder()
      .uid(user.getUid())
      .username(user.getUsername())
      .email(user.getEmail())
      .isEmailVerified(user.getIsEmailVerified())
      .role(user.getRole())
      .penname(user.getPenname())
      .profileImageUrl(user.getProfileImageUrl())
      .build();

    return dto;
  }
}
