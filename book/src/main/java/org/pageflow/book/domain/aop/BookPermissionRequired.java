package org.pageflow.book.domain.aop;

import org.pageflow.book.domain.enums.BookPermissionPolicy;
import org.pageflow.book.port.in.token.BookPermission;

import java.lang.annotation.*;

/**
 * {@link BookPermission}이 요구되는 권한을 가지고있는지 검증하기 위한 어노테이션
 *
 * {@link org.pageflow.book.port.in.token.BookContext} 타입을 매개받는 메소드에 부착하면 AOP를 통하여 요구되는 권한을 충족하는지 검사한다.
 * @author : sechan
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BookPermissionRequired {
  BookPermissionPolicy[] value() default { BookPermissionPolicy.FULL };
}
