package org.pageflow.common.module;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.*;

/**
 * 프로젝트 모듈의 entry point 설정 파일에 부착하는 어노테이션
 * @author : sechan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
public @interface ApplicationModule {
}
