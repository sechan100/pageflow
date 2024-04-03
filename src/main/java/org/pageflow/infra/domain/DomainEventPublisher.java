package org.pageflow.infra.domain;

/**
 * @author : sechan
 */
public interface DomainEventPublisher {

    void raise(DomainEvent event);
}
