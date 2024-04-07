package org.pageflow.shared.infra.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author : sechan
 */
@Component
@RequiredArgsConstructor
public class DomainEventPublisherImpl implements DomainEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void raise(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
