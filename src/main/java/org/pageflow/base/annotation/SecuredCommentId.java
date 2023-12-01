package org.pageflow.base.annotation;

import java.lang.annotation.*;

/**
 * commentId에 매핑되는 메소드 파라미터에 붙여서, 어떤 파라미터가 접근권한이 확인되어야하는 commentId인지 명시하는 어노테이션
 * @author : sechan
 * @see org.pageflow.base.aop.CommentAccessAspect
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredCommentId {
    boolean adminOnly() default false; // 관리자만 접근 가능한지 여부 -> true라면 아무리 엔티티 주인이라고해도 접근 불가능
}
