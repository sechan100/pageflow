package org.pageflow.shared.infra.domain;

/**
 * @author : sechan
 */
public interface AggregateFactory {
    <T extends AggregateRoot, ID> T create(Class<T> clazz, ID id);
}
