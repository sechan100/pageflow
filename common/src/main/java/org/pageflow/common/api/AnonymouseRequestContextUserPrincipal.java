package org.pageflow.common.api;

import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;

/**
 * 익명 사용자의 Principal을 구현하는 객체
 * @author : sechan
 */
public class AnonymouseRequestContextUserPrincipal implements RequestContextUserPrincipal {

  @Override
  public UID getUid() {
    return UID.ANONYMOUS_UID;
  }

  @Override
  public RoleType getRole() {
    return RoleType.ROLE_ANONYMOUS;
  }
}
