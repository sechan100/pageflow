package org.pageflow.infra.domain;

import org.springframework.transaction.support.TransactionSynchronization;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : sechan
 */
public class TransactionDomainEventQueue implements TransactionSynchronization {
    private final List<DomainEvent> events;
    private final DomainEventPublisher publisher;

    public TransactionDomainEventQueue(DomainEventPublisher publisher){
        this.events = new LinkedList<>();
        this.publisher = publisher;
    }

    public void add(DomainEvent event){
        events.add(event);
    }

    public List<DomainEvent> getEvents(){
        return Collections.unmodifiableList(events);
    }

    @Override
    public void beforeCommit(boolean readOnly){
        events.forEach(publisher::raise);
    }

    @Override
    public void afterCompletion(int status){
        events.clear();
    }
}
