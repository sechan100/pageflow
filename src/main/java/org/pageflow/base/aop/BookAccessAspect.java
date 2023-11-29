package org.pageflow.base.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.base.annotation.SecuredBookId;
import org.pageflow.base.exception.data.ApiEntityAccessDeniedException;
import org.pageflow.base.exception.data.WebEntityAccessDeniedException;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author : sechan
 */
@Aspect
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookAccessAspect {
    
    private final Rq rq;
    private final BookService bookService;
    
    @Before("execution(* org.pageflow.domain.book.controller.*.*(.., @org.pageflow.base.annotation.SecuredBookId (*), ..))")
    public void beforeBookAccess(JoinPoint joinPoint) {
        String classPackageFullPath = joinPoint.getSignature().getDeclaringTypeName();
        String className = classPackageFullPath.substring(classPackageFullPath.lastIndexOf('.') + 1);
        boolean isApiClass = className.startsWith("Api");
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof SecuredBookId && parameterTypes[i] == Long.class) {
                    Object arg = joinPoint.getArgs()[i];
                    Long bookId = (Long) arg;
                    Book book = bookService.repoFindBookWithAuthorById(bookId);
                    Long userId = rq.getUserSession().getId();
                    if (!book.getAuthor().getId().equals(userId)) {
                        if(isApiClass){
                            throw new ApiEntityAccessDeniedException();
                        } else {
                            throw new WebEntityAccessDeniedException();
                        }
                    }
                    return;
                }
            }
        }
    }
}
