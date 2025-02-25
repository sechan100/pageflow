package org.pageflow.book.domain.aop;

import org.aspectj.lang.JoinPoint;
import org.pageflow.book.port.in.token.BookContext;

/**
 * @author : sechan
 */
public abstract class BookAopUtils {

  public static BookContext extractBookContext(JoinPoint joinPoint){
    Object[] args = joinPoint.getArgs();
    BookContext result = null;
    for(Object arg : args){
      if(arg instanceof BookContext context){
        if(result == null){
          result = context;
        } else {
          throw new IllegalStateException("메소드에 BookContext 타입의 파라미터가 2개 이상 존재합니다.");
        }
      }
    }
    if(result == null){
      throw new IllegalStateException("메소드에 BookContext 타입의 파라미터가 존재하지 않습니다.");
    }
    return result;
  }
}
