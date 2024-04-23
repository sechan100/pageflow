package org.pageflow.shared.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping
public @interface WebAdapter {
    @AliasFor(annotation = RestController.class)
    String value() default "";

    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};
}
