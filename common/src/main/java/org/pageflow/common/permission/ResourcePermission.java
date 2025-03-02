package org.pageflow.common.permission;

import java.util.Set;

/**
 * @author : sechan
 */
public interface ResourcePermission<ID> {
  /**
   * 해당 객체가 가지고있는 허가된 action들을 가지고온다.
   * @return
   */
  Set<? extends ResourceAction> getPermittedActions();

  /**
   * @return 가지고있는 ResourceAction의 모든 Action들을 허가하는 경우 true를 반환해야한다.
   */
  boolean isFullActionPermitted();

  ID getResourceId();
}
