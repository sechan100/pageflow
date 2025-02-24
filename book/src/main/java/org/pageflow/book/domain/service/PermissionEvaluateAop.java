package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.book.adapter.out.AuthorAcl;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookPermissionPolicy;
import org.pageflow.book.domain.BookPermissionRequired;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.port.in.BookPermission;
import org.pageflow.book.port.in.BookResourcePermitter;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author : sechan
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionEvaluateAop implements BookResourcePermitter {
  private final BookPersistencePort bookPersistencePort;
  private final AuthorAcl authorAcl;

  /**
   * {@link BookPermissionRequired} 어노테이션이 부착된 모든 메소드를 대상으로 한다.
   */
  @Pointcut("@annotation(org.pageflow.book.domain.BookPermissionRequired)")
  public void bookPermissionRequiredMethods(){}


  @Before("bookPermissionRequiredMethods()")
  public void evaluatePermission(JoinPoint joinPoint){
    BookPermission permission = extractPermission(joinPoint);
    BookPermissionPolicy[] appliedPolicies = extractPolicy(joinPoint);

    // 권한 평가
    assert appliedPolicies != null;
    for(BookPermissionPolicy policy : appliedPolicies){
      if(!permission.isPermitted(policy)){
        throw new ProcessResultException(Result.of(BookCode.BOOK_ACCESS_DENIED, policy));
      }
    }
  }

  @Override
  public BookPermission getAuthorPermission(UUID bookId, UID uid) {
    Book book = bookPersistencePort.findById(bookId).get();
    Author author = authorAcl.loadAuthorReference(uid);
    return BookPermission.authorPermission(book, author);
  }



  private BookPermission extractPermission(JoinPoint joinPoint){
    Object[] args = joinPoint.getArgs();
    BookPermission result = null;
    for(Object arg : args){
      if(arg instanceof BookPermission permission){
        if(result == null){
          result = permission;
        } else {
          throw new IllegalStateException("메소드에 BookPermission 타입의 파라미터가 2개 이상 존재합니다.");
        }
      }
    }
    if(result == null){
      throw new IllegalStateException("메소드에 BookPermission 타입의 파라미터가 존재하지 않습니다.");
    }
    return result;
  }

  private BookPermissionPolicy[] extractPolicy(JoinPoint joinPoint){
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    BookPermissionRequired annotation = method.getAnnotation(BookPermissionRequired.class);
    if(annotation!=null){
      BookPermissionPolicy[] policies = annotation.value();
      return policies;
    } else {
      assert false:"BookPermissionRequired 어노테이션을 찾을 수 없음.";
      return null;
    }
  }

}
