package org.pageflow.common.permission;


import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceId {
  /**
   * SpEL을 이용하여 ResourceId를 지정한다.
   * 중첩된 객체 구조가 아닌 경우, 파라미터 앞에 붙이는 것만으로도 충분하다.
   * @return
   */
  String value() default "";
  Class<? extends ResourcePermission> permissionType();
}
