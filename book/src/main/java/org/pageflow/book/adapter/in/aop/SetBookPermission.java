package org.pageflow.book.adapter.in.aop;

import org.pageflow.book.application.BookId;

import java.lang.annotation.*;

/**
 * 해당 어노테이션이 부착된 Controller 메소드는 {@link org.pageflow.book.port.in.token.BookPermission}을 자동으로 할당한다.
 * @apiNote BookId에 해당하는 파라미터를 {@link BookId}를 이용하여 지정해야한다.
 * @author : sechan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetBookPermission {

}
