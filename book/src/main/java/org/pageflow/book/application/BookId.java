package org.pageflow.book.application;

import org.pageflow.book.port.in.token.BookPermission;
import org.pageflow.common.permission.ResourceId;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ResourceId(permissionType = BookPermission.class)
public @interface BookId {

  @AliasFor(annotation = ResourceId.class, attribute = "value")
  String value() default "";
}
