package org.pageflow.common.permission;

import java.util.List;

/**
 * 특정 요청, 일반적으로는 해당 리소스에 접근하는 주체(사용자)가 보장되는 context 안에서만 유지되는 permission들을 관리한다.
 * @author : sechan
 */
public interface ResourcePermissionAware {
  /**
   * @implSpec 동일한 타입의 permission이 2개 이상 존재하도록 해서는 안된다.
   * 특정 리소스에 대한 permission 객체는 Context에서 단 1개만 존재해야한다.
   * @param permission
   */
  void addResourcePermission(ResourcePermission permission);

  List<ResourcePermission> getResourcePermissions();
}
