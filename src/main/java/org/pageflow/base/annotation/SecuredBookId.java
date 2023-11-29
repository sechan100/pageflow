package org.pageflow.base.annotation;

import java.lang.annotation.*;

/**
 * bookId에 매핑되는 메소드 파라미터에 붙여서, 어떤 파라미터가 접근권한이 확인되어야하는 bookId인지 명시하는 어노테이션
 * @author : sechan
 * @see org.pageflow.base.aop.BookAccessAspect
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredBookId {
}
