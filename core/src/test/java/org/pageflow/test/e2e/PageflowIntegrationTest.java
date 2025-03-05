package org.pageflow.test.e2e;

import org.junit.jupiter.api.extension.ExtendWith;
import org.pageflow.core.PageflowApplication;
import org.pageflow.test.shared.TestModuleConfig;
import org.pageflow.test.shared.extension.DbClearExtension;
import org.pageflow.test.shared.extension.ResourcePermissionClearExtension;
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
@ExtendWith({DbClearExtension.class, ResourcePermissionClearExtension.class})
@Inherited
public @interface PageflowIntegrationTest {
}
