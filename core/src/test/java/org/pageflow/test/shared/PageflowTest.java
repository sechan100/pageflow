package org.pageflow.test.shared;

import org.junit.jupiter.api.extension.ExtendWith;
import org.pageflow.core.PageflowApplication;
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
@SpringBootTest(classes = PageflowApplication.class)
@Import(TestModuleConfig.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith({ResourcePermissionClearExtension.class})
@Transactional
@Inherited
public @interface PageflowTest {
}
