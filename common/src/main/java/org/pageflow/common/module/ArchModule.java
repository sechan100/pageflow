package org.pageflow.common.module;


import java.lang.annotation.Annotation;

/**
 * Test에서 property 이름을 가져와 검증을 진행하기에, property 이름을 함부로 변경하지 않도록 주의한다.
 */
public @interface ArchModule {
    /**
     * 모듈 이름(소문자)
     */
    String name();

    /**
     * 외부에 노출시킬 패키지
     */
    String[] exposedPackages() default {};

    /**
     * 외부에 노출시킬 클래스에 부착된 어노테이션 목록.
     * 해당 어노테이션이 부착된 클래스는 외부에서 접근 가능하게된다.
     */
    Class<? extends Annotation>[] exposedClassAnnotation() default {};
}