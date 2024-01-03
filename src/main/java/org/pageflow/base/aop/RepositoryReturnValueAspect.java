package org.pageflow.base.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pageflow.base.exception.UserFeedbackException;
import org.pageflow.base.exception.code.CommonErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Aspect
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepositoryReturnValueAspect {
    
    
    // 모든 find로 시작하는 메소드에 대한 Aspect를 실행하여, 반환이 null이거나 비어있는 컬렉션일 경우 예외를 발생시킨다.
    @Around("execution(* org.pageflow.domain.*.repository.*.find*(..))")
    public Object handleRepositoryReturnValue(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // 메소드 실행
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        String arsString = "(" + Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", ")) + ")";
        String entityName = joinPoint.getSignature().getDeclaringType().getSimpleName().replace("Repository", "");
        // 단일 엔티티 객체일 경우의 처리
        if(result == null){
            throw new UserFeedbackException(CommonErrorCode.DATA_NOT_FOUND, entityName + "를 다음으로 조회: " + arsString);
            
        // 컬렉션 객체일 경우의 처리
        } else if(result instanceof Collection<?> results){
            if(results.isEmpty()){
                throw new UserFeedbackException(CommonErrorCode.DATA_NOT_FOUND, entityName + "컬렉션을 다음으로 조회: " + arsString);
            }
        } else if(result instanceof Optional<?> optional){
            if(optional.isEmpty()){
                throw new UserFeedbackException(CommonErrorCode.DATA_NOT_FOUND, entityName + "를 다음으로 조회: " + arsString);
            }
        }
        
        return result;
    }
    
}
