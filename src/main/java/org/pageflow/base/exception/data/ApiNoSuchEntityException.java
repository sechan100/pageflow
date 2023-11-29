package org.pageflow.base.exception.data;

import lombok.Getter;
import org.pageflow.base.entity.BaseEntity;

/**
 * 엔티티 부재 예외 -> RestControllerAdvice에서 일괄 처리
 * @author : sechan
 */
@Getter
public class ApiNoSuchEntityException extends NoSuchEntityException {
    public <T extends BaseEntity> ApiNoSuchEntityException(Class<T> clazz) {
        super(clazz);
    }
    
    public ApiNoSuchEntityException(NoSuchEntityException e){
        super(e.getEntityClass());
    }
}
