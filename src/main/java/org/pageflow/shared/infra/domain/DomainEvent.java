package org.pageflow.shared.infra.domain;

import org.springframework.context.ApplicationEvent;

/**
 * @author : sechan
 */
public abstract class DomainEvent extends ApplicationEvent {

    public DomainEvent(AggregateRoot source) {
        super(source);
    }

    public AggregateRoot getAggregateRoot() {
        return (AggregateRoot) getSource();
    }
}
