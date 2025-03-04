package org.pageflow.test.e2e.data;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Transactional
public @interface Fixture {
    Class<? extends DataFixture>[] datas();
}