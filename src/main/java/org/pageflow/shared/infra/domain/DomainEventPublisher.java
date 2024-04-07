package org.pageflow.shared.infra.domain;

/**
 * @author : sechan
 */
public interface DomainEventPublisher {
    void raise(DomainEvent event);
}
