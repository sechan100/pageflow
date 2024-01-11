package org.pageflow.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.pageflow.global.entity.DataNotFoundException;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

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
public class RepositoryReturnValueAspect {
    
    
    // 모든 find로 시작하는 메소드에 대한 Aspect를 실행하여, 반환이 null이거나 비어있는 컬렉션일 경우 예외를 발생시킨다.
    @Around("execution(* org.pageflow.domain.*.repository.*.find*(..))")
    public Object handleRepositoryReturnValue(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // 메소드 실행
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        String argString = "(" + Arrays.stream(args).map(Object::toString).collect(Collectors.joining(", ")) + ")";
        String entityName = joinPoint.getSignature().getDeclaringType().getSimpleName().replace("Repository", "");
        
        // 1. 단일 엔티티 객체일 경우의 처리
        if(result == null){
            throw new DataNotFoundException(entityName + "을(를) 다음으로 조회:" + argString);
            
        // 2. 컬렉션 객체일 경우의 처리
        } else if(result instanceof Collection<?> results){
            if(results.isEmpty()){
                throw new DataNotFoundException(entityName + "컬렉션을 다음으로 조회: " + argString);
            }
        // 3. Optional 객체일 경우의 처리
        } else if(result instanceof Optional<?> optional){
            if(optional.isEmpty()){
                throw new DataNotFoundException(entityName + "를 다음으로 조회: " + argString);
            }
        // 4. Slice, Page 객체일 경우의 처리
        } else if(result instanceof Slice<?> sliceOrPage){
            if(sliceOrPage.isEmpty()){
                throw new DataNotFoundException(entityName + "페이지(slice를) 다음으로 조회: " + argString);
            }
        }
        
        return result;
    }
    
}
