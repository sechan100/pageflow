package org.pageflow.test.e2e.shared.fixture;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Transactional
public @interface Fixture {
    Class<? extends TestFixture>[] value();
}