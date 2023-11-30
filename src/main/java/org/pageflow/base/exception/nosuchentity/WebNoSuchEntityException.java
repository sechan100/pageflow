package org.pageflow.base.exception.nosuchentity;

import org.pageflow.base.entity.BaseEntity;
import org.pageflow.base.exception.nosuchentity.NoSuchEntityException;

/**
 * 엔티티 부재 예외 -> ControllerAdvice에서 일괄 처리
 * @author : sechan
 */
public class WebNoSuchEntityException extends NoSuchEntityException {
    
    public <T extends BaseEntity> WebNoSuchEntityException(Class<T> clazz) {
        super(clazz);
    }
    
    public WebNoSuchEntityException(NoSuchEntityException e){
        super(e.getEntityClass());
    }
}
