package org.pageflow.infra.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisherLocator {
    private static DomainEventPublisher lazyDomainEventPublisher = null;

    @Autowired
    public DomainEventPublisherLocator(DomainEventPublisher domainEventPublisher){
        // 정적 필드 lazy 초기화
        DomainEventPublisherLocator.lazyDomainEventPublisher = domainEventPublisher;
    }

    // package-private
    static DomainEventPublisher getPublisher(){
        assert lazyDomainEventPublisher!= null: "PublisherLocator is not initialized";
        return DomainEventPublisherLocator.lazyDomainEventPublisher;
    }
}