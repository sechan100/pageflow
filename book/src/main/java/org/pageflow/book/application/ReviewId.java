package org.pageflow.book.application;

import org.pageflow.book.domain.ReviewPermission;
import org.pageflow.common.permission.ResourceId;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@ResourceId(permissionType = ReviewPermission.class)
public @interface ReviewId {

  @AliasFor(annotation = ResourceId.class, attribute = "value")
  String value() default "";
}
