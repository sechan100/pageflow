package org.pageflow.test.e2e.shared;

import org.pageflow.core.PageflowApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : sechan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = PageflowApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestModuleConfig.class)
@Inherited
public @interface PageflowIntegrationTest {
}
