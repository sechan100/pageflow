package org.pageflow.common.permission;


import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Documented
@Target(ElementType.METHOD)
@Repeatable(ContainerPermissionRequired.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionRequired {

  /**
   * {@link ResourceAction}를 구현하는 enum의 name을 사용할 수 있다.
   * 또는 "FULL"을 사용하여 특정 enum set의 모든 권한을 필요로 함을 표현할 수 있다.
   *
   * @return
   */
  String[] actions();

  Class<? extends ResourcePermission> permissionType();
}
