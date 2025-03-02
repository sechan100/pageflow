package org.pageflow.book.adapter.in.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.common.aop.JoinPointSpELDynamicValueExtractor;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.permission.ResourcePermissionAware;
import org.pageflow.common.user.UID;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * book에 접근하는 컨트롤러가 실행될 때, 자동으로 Permission 객체를 컨텍스트에 할당하는 AOP
 * @see SetBookPermission
 * @author : sechan
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BookAccessWebAdapterPermittionSettingAop {
  private final BookAccessPermitter bookAccessPermitter;
  private final ResourcePermissionAware resourcePermissionAware;
  private final RequestContext rqcxt;


  /**
   * {@link SetBookPermission}가 붙은 메서드에 적용
   */
  @Pointcut("@annotation(org.pageflow.book.adapter.in.aop.SetBookPermission)")
  public void pointcut() {}


  @Before("pointcut()")
  public void setBookPermission(JoinPoint joinPoint) {
    UUID bookId = extractBookId(joinPoint);
    UID uid = rqcxt.getUid();
    BookPermission permission = bookAccessPermitter.grant(bookId, uid);
    resourcePermissionAware.addResourcePermission(permission);
  }

  private UUID extractBookId(JoinPoint joinPoint){
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Object[] args = joinPoint.getArgs();
    String[] parameterNames = signature.getParameterNames();

    for(int i = 0; i < method.getParameterCount(); i++){
      // BookId 어노테이션이 부착된 매개변수를 찾는다.
      BookId bookIdAnnotation = AnnotatedElementUtils.findMergedAnnotation(method.getParameters()[i], BookId.class);
      if(bookIdAnnotation == null){
        continue;
      }

      String SpEL = bookIdAnnotation.value();
      // SpEL 표현식이 지정된 경우
      if(!SpEL.isEmpty()){
        JoinPointSpELDynamicValueExtractor extractor = new JoinPointSpELDynamicValueExtractor(joinPoint);
        return (UUID) extractor.getDynamicValue(SpEL);

        // 표현식이 비어있다면 해당 매개변수가 bookId임
      } else {
        return (UUID) args[i];
      }
    }
    throw new IllegalArgumentException("@BookId 어노테이션이 부착된 매개변수를 찾을 수 없습니다.");
  }
}
