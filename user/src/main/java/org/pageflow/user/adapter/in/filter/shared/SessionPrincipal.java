package org.pageflow.user.adapter.in.filter.shared;

import lombok.Getter;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.api.RequestContextUserPrincipal;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;

/**
 * 세션을 유지중인 사용자에 대한 정보를 저장하는 객체.
 * {@link RequestContext}등에서 접근된다.
 * @author : sechan
 */
@Getter
public class SessionPrincipal implements RequestContextUserPrincipal {
  private final UID uid;
  private final RoleType role;

  public SessionPrincipal(UID uid, RoleType roleType) {
    this.uid = uid;
    this.role = roleType;
  }
}
