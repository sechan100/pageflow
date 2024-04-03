package org.pageflow.infra.domain;

import lombok.Getter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author : sechan
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


    public void raise(DomainEvent event) {
        if(TransactionSynchronizationManager.isActualTransactionActive()){
            eventQueue.add(event);
        } else {
            publisher.raise(event);
        }
    }


}
