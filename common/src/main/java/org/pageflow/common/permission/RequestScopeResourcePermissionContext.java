package org.pageflow.common.permission;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : sechan
 */
@Component
@RequestScope
public class RequestScopeResourcePermissionContext implements ResourcePermissionContext {
  private final List<ResourcePermission> permissions;

  public RequestScopeResourcePermissionContext() {
    this.permissions = new ArrayList<>();
  }


  @Override
  public void addResourcePermission(ResourcePermission permission) {
    if(permissions.stream().anyMatch(p -> p.getClass().equals(permission.getClass()))){
      throw new IllegalArgumentException("동일한 타입의 permission이 2개 이상 존재하도록 해서는 안됩니다. class: " + permission.getClass().getName());
    }
    permissions.add(permission);
  }

  @Override
  public List<ResourcePermission> getResourcePermissions() {
    return Collections.unmodifiableList(permissions);
  }
}
