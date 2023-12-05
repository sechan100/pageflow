package org.pageflow.base.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.base.annotation.SecuredCommentId;
import org.pageflow.base.exception.entityaccessdenied.ApiEntityAccessDeniedException;
import org.pageflow.base.exception.entityaccessdenied.WebEntityAccessDeniedException;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.service.CommentService;
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
public class CommentAccessAspect {
    
    private final Rq rq;
    private final CommentService commentService;
    
    @Before("execution(* org.pageflow.domain.*.controller.*.*(.., @org.pageflow.base.annotation.SecuredCommentId (*), ..))")
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
                if (annotation instanceof SecuredCommentId && parameterTypes[i] == Long.class) {
                    // 관리자만 접근 가능한지 여부
                    boolean isAdminOnlyAccessible = ((SecuredCommentId) annotation).adminOnly();
                    if(isAdminOnlyAccessible) {
                        if(!rq.getUserSession().isAdmin()){
                            if(isApiClass){
                                throw new ApiEntityAccessDeniedException();
                            } else {
                                throw new WebEntityAccessDeniedException();
                            }
                        }
                    }
                    
                    
                    Object arg = joinPoint.getArgs()[i];
                    Long commentId = (Long) arg;
                    Comment comment = commentService.repoFindCommentWithInteractorById(commentId);
                    Long userId = rq.getUserSession().getId();
                    if (!comment.getInteractor().getId().equals(userId)) {
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
