package org.pageflow.book.domain.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.BookStatusRequirement;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Aspect
@Component
@RequiredArgsConstructor
public class CheckBookStatusAop {
  private final BookPersistencePort bookPersistencePort;


  /**
   * {@link CheckBookStatus}가 붙은 메서드에 적용
   */
  @Pointcut("@annotation(org.pageflow.book.domain.aop.CheckBookStatus)")
  public void checkBookStatusPointcut() {}


  @Before("checkBookStatusPointcut()")
  public void checkBookStatus(JoinPoint joinPoint){
    UUID bookId = BookAopUtils.extractBookContext(joinPoint).getBookId();
    Book book = bookPersistencePort.findById(bookId).get();
    List<BookStatus> requiredStatuses = List.of(extractRequiredBookStatus(joinPoint));

    if(!requiredStatuses.contains(book.getStatus())){
      throw new ProcessResultException(Result.of(
        BookCode.INVALID_BOOK_STATUS, BookStatusRequirement.of(book.getStatus(), requiredStatuses)
      ));
    }
  }


  private BookStatus[] extractRequiredBookStatus(JoinPoint joinPoint){
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    CheckBookStatus checkBookStatus;
    if(method.isAnnotationPresent(CheckBookStatus.class)){
      checkBookStatus = method.getAnnotation(CheckBookStatus.class);
    } else {
      throw new IllegalStateException("CheckBookStatus 어노테이션을 찾을 수 없음.");
    }

    assert checkBookStatus != null;
    return checkBookStatus.value();
  }

}
