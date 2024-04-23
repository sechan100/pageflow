package org.pageflow.shared.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author : sechan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Adapter
public @interface PersistenceAdapter {
    @AliasFor(annotation = Adapter.class)
    String value() default "";
}
