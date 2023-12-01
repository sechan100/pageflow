package org.pageflow.domain.interaction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.user.entity.Profile;

/**
 * @author : sechan
 * Interaction 도메인상에서 사용하는 표준화된 상호작용 쌍을 표현하는 클래스
 * 상호작용의 주체와 타겟을 포함한다.
 */
@Getter
public class InteractionPair<T extends BaseEntity> {
    
    @JsonIgnore
    private Profile interactor;
    @JsonIgnore
    private T target;
    
    
    private Long interactorId;
    private Long targetId;
    private String targetType;
    
    
    /**
     * @param interactor 상호작용의 주체
     * @param target    상호작용의 타겟
     */
    public InteractionPair(Profile interactor, T target) {
        this.interactor = interactor;
        this.target = target;
        this.interactorId = interactor.getId();
        this.targetId = target.getId();
        this.targetType = target.getClass().getSimpleName();
    }
    
    public void setInteractor(Profile interactor) {
        this.interactor = interactor;
        this.interactorId = interactor.getId();
    }
    
    public void setTarget(T target) {
        this.target = target;
        this.targetId = target.getId();
        this.targetType = target.getClass().getSimpleName();
    }
}
