package org.pageflow.test.shared;

import org.pageflow.common.permission.ResourcePermission;
import org.pageflow.common.permission.ResourcePermissionContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : sechan
 */
@Component
@Primary
public class TestResourcePermissionContext implements ResourcePermissionContext {
  private final List<ResourcePermission> permissions;

  public TestResourcePermissionContext() {
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

  public void clear() {
    permissions.clear();
  }
}
