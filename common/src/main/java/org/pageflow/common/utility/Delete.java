package org.pageflow.common.utility;

import org.pageflow.common.api.ApiAccess;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(
  method = {RequestMethod.DELETE}
)
@Secured({})
public @interface Delete {

  @AliasFor(
    annotation = Secured.class,
    attribute = "value"
  )
  String[] access() default ApiAccess.USER;


  // @PostMapping alias
  @AliasFor(
    annotation = RequestMapping.class
  )
  String name() default "";

  @AliasFor(
    annotation = RequestMapping.class
  )
  String[] value() default {};

  @AliasFor(
    annotation = RequestMapping.class
  )
  String[] path() default {};

  @AliasFor(
    annotation = RequestMapping.class
  )
  String[] params() default {};

  @AliasFor(
    annotation = RequestMapping.class
  )
  String[] headers() default {};

  @AliasFor(
    annotation = RequestMapping.class
  )
  String[] consumes() default {};

  @AliasFor(
    annotation = RequestMapping.class
  )
  String[] produces() default {};
}
