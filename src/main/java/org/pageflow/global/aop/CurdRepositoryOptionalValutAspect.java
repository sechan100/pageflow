package org.pageflow.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Aspect
@Component
@RequiredArgsConstructor
public class CurdRepositoryOptionalValutAspect {


    /**
     * Optional으로 반환되는 findById 메소드의 Optional을 이미 까봐서 없으면 NoSuchElementException이 발생히기 전에
     * EmptyResultDataAccessException으로 먼저 던져버린다.
     */
    @Around("execution(* org.springframework.data.repository.CrudRepository.findById(..))")
    public Object handleFindByIdReturnValue(ProceedingJoinPoint joinPoint) throws Throwable {

        // 메소드 실행
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();
        String argString = Arrays.stream(args)
            .map(Object::toString)
            .collect(Collectors.joining(", "));

        if(result instanceof Optional<?> optional) {
            optional.orElseThrow(() -> new EmptyResultDataAccessException("No value present for " + argString, 1));
        }

        return result;
    }

}
