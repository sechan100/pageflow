package org.pageflow.book.domain.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.book.adapter.out.AuthorAcl;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.enums.BookPermissionPolicy;
import org.pageflow.book.port.in.token.BookPermission;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author : sechan
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionEvaluateAop {
  private final BookPersistencePort bookPersistencePort;
  private final AuthorAcl authorAcl;

  /**
   * {@link BookPermissionRequired} 어노테이션이 부착된 모든 메소드를 대상으로 한다.
   */
  @Pointcut("@annotation(org.pageflow.book.domain.aop.BookPermissionRequired)")
  public void bookPermissionRequiredMethods(){}


  @Before("bookPermissionRequiredMethods()")
  public void evaluatePermission(JoinPoint joinPoint){
    BookPermission permission = BookAopUtils.extractBookContext(joinPoint).getPermission();
    BookPermissionPolicy[] appliedPolicies = extractPolicy(joinPoint);

    // 권한 평가
    assert appliedPolicies != null;
    for(BookPermissionPolicy policy : appliedPolicies){
      if(!permission.isPermitted(policy)){
        throw new ProcessResultException(Result.of(BookCode.BOOK_ACCESS_DENIED, policy));
      }
    }
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
