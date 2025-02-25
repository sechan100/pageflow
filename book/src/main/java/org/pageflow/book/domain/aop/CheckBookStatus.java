package org.pageflow.book.domain.aop;

import org.pageflow.book.domain.enums.BookStatus;

import java.lang.annotation.*;

/**
 * 해당 어노테이션이 부착된 메서드는 BookContext의 book이 명시된 BookStatus인지 확인하는 로직을 적용한다.
 *
 * <p>
 *   메서드는 반드시 {@link org.pageflow.book.port.in.token.BookContext}를 매개받아야한다.
 * </p>
 * @author : sechan
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckBookStatus {
  /**
   * 여러개가 존재하는 경우 or 조건
   * @return
   */
  BookStatus[] value();
}
