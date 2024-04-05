package org.pageflow.infra.domain;

import lombok.Getter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * <p>REVIEW: 도메인 모델에서 이벤트의 발행시점을 조정하는게 올바른건지 모르곘음. @TransactionalEventListener를 사용하는 방안도 존재.</p>
 */
public abstract class AggregateRoot<ID> {
    @Getter
    private final ID id;
    private final TransactionDomainEventQueue eventQueue;
    private final DomainEventPublisher publisher;

    protected AggregateRoot(ID id){
        this.id = id;
        this.publisher = DomainEventPublisherLocator.getPublisher();
        this.eventQueue = new TransactionDomainEventQueue(this.publisher);
        TransactionSynchronizationManager.registerSynchronization(eventQueue);
    }


    public void raiseEvent(DomainEvent event) {
        publisher.raise(event);
    }

    /**
     * <p>트랜잭션이 활성화 되어있다면, eventQueue에 이벤트를 등록합니다.
     * eventQueue는 beforeCommit에 flush됩니다.</p>
     *
     * <p>만약 트랜잭션이 시작되지 않았다면, 즉시 이벤트를 발행합니다.
     * </p>
     * @see AggregateRoot#registerEvent(DomainEvent)
     */
    public void registerEvent(DomainEvent event) {
        if(TransactionSynchronizationManager.isActualTransactionActive()){
            eventQueue.add(event);
        } else {
            raiseEvent(event);
        }
    }

}
