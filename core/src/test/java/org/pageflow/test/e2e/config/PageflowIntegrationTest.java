package org.pageflow.test.e2e.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.pageflow.core.PageflowApplication;
import org.pageflow.test.e2e.data.FixtureExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

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
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@ExtendWith(FixtureExtension.class)
@Inherited
public @interface PageflowIntegrationTest {
}
