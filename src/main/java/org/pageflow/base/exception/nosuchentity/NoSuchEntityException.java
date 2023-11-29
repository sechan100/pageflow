package org.pageflow.base.exception.nosuchentity;

import lombok.Getter;

/**
 * 엔티티 부재 예외 -> ApiNoSuchEntityException, WebNoSuchEntityException으로 분리
 * @author : sechan
 */
@Getter
public class NoSuchEntityException extends RuntimeException {
    
    private final Class<?> entityClass;
    
    public NoSuchEntityException(Class<?> clazz) {
        super(String.format("쿼리된 '%s' 엔티티를 찾을 수 없습니다.", clazz.getSimpleName()));
        this.entityClass = clazz;
    }
    
    public NoSuchEntityException(Class<?> clazz, String message) {
        super(message);
        this.entityClass = clazz;
    }
}
