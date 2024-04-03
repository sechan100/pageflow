package org.pageflow.infra.domain;

import org.springframework.context.ApplicationEvent;

/**
 * @author : sechan
 */
public abstract class DomainEvent extends ApplicationEvent {
    public DomainEvent(AggregateRoot source) {
        super(source);
    }
}
