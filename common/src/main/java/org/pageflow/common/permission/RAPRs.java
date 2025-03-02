package org.pageflow.common.permission;


import java.lang.annotation.*;

/**
 * {@link ResourceAccessPermissionRequired}를 @Repeatable로 사용하기 위한 Wrapper Annotation
 * @author : sechan
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RAPRs {
  ResourceAccessPermissionRequired[] value();
}
