package org.pageflow.user.adapter.in.res;

import lombok.Value;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.dto.AccountDto;
import org.pageflow.user.dto.IdentifiableUser;

/**
 * @author : sechan
 */
@Value
public class AccountRes implements IdentifiableUser {
  UID uid;
  String username;
  String email;
  boolean isEmailVerified;
  ProviderType provider;
  RoleType role;


  public static AccountRes from(AccountDto dto){
    return new AccountRes(
      dto.getUid(),
      dto.getUsername(),
      dto.getEmail(),
      dto.isEmailVerified(),
      dto.getProvider(),
      dto.getRole()
    );
  }
}
