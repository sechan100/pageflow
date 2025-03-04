package org.pageflow.common.permission;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Documented
@Target(ElementType.METHOD)
@Repeatable(RAPRs.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionRequired {

  @AliasFor("actions")
  String[] value() default {"FULL"};

  @AliasFor("value")
  String[] actions() default {"FULL"};

  Class<? extends ResourcePermission> permissionType();
}
