package org.pageflow.base.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.pageflow.base.exception.data.ApiNoSuchEntityException;
import org.pageflow.base.exception.data.NoSuchEntityException;
import org.pageflow.base.exception.data.WebNoSuchEntityException;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Aspect
@Component
@Slf4j
public class NoSuchEntityAspect {
    
    // Api @RestController가 아닌 일반 @Controller 클래스에 관한 Aspect
    @Pointcut("within(org.pageflow.domain.*.controller.*) && !within(org.pageflow.domain.*.controller.Api*)")
    public void webControllerClassesAspect() {}
    
    
    @AfterThrowing(pointcut = "webControllerClassesAspect()", throwing = "e")
    public void handleWebNoSuchEntityException(JoinPoint joinPoint, NoSuchEntityException e) {
        log.error("web endPoint에서 NoSuchEntityException 예외가 발생: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()  + " \"" + e.getMessage() +"\"");
        throw new WebNoSuchEntityException(e);
    }
    
    
    
    
    // Api @RestController 클래스에 관한 Aspect
    @Pointcut("within(org.pageflow.domain.*.controller.Api*)")
    public void apiControllerClassesAspect() {}
    
    
    @AfterThrowing(pointcut = "apiControllerClassesAspect()", throwing = "e")
    public void handleApiNoSuchEntityException(JoinPoint joinPoint, NoSuchEntityException e) {
        log.error("api endPoint에서 NoSuchEntityException 예외가 발생: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()  + " \"" + e.getMessage() +"\"");
        
        // ApiNoSuchEntityException으로 변환하여 throwing
        // 새로 발생한 ApiNoSuchEntityException은 @RestControllerAdvice에서 처리된다.
        throw new ApiNoSuchEntityException(e);
    }
}
