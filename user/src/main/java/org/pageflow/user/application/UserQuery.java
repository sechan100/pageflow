package org.pageflow.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Account;
import org.pageflow.user.domain.entity.Profile;
import org.pageflow.user.dto.SessionUserDto;
import org.pageflow.user.port.out.LoadSessionUserPort;
import org.pageflow.user.port.out.entity.AccountPersistencePort;
import org.springframework.stereotype.Service;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserQuery implements LoadSessionUserPort {
  private final AccountPersistencePort accountPersistencePort;

  @Override
  public SessionUserDto load(UID uid) {
    Account account = accountPersistencePort.findWithProfileById(uid.getValue()).get();
    Profile profile = account.getProfile();

    SessionUserDto dto = SessionUserDto.builder()
      .uid(account.getUid())
      .username(account.getUsername())
      .email(account.getEmail())
      .isEmailVerified(account.getIsEmailVerified())
      .role(account.getRole())
      .penname(profile.getPenname())
      .profileImageUrl(profile.getProfileImageUrl())
      .build();

    return dto;
  }
}
