package org.pageflow.user.dto;

import lombok.Value;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Account;

/**
 * @author : sechan
 */
@Value
public class AccountDto implements IdentifiableUser {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  ProviderType provider;
  RoleType role;

  public static AccountDto from(Account account){
    return new AccountDto(
      account.getUid(),
      account.getUsername(),
      account.getEmail(),
      account.getIsEmailVerified(),
      account.getProvider(),
      account.getRole()
    );
  }
}
