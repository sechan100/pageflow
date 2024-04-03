package org.pageflow.infra.domain;

/**
 * @author : sechan
 */
public interface AggregateRootFactory {
    <T extends AggregateRoot, ID> T create(Class<T> clazz, ID id) throws InstantiationException, IllegalAccessException;
}
