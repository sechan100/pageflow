package org.pageflow.shared.query;

import org.pageflow.global.entity.Entity;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author : sechan
 */
public interface TryQuery<T> {
    
    static <T extends Entity> TryQuery<T> of(Supplier<T> query){
        return new VavrTryQueryImpl<>(query);
    }

    T find() throws EmptyResultDataAccessException;
    T findOrElse(T defaultValue);
    T findOrElse(Supplier<T> defaultValueProvider);
    T findOrElseThrow(Supplier<RuntimeException> exceptionSupplier);
    TryQuery<T> onFind(Consumer<T> onFindCallback);
    TryQuery<T> onNotFound(Runnable onNotFoundCallback);
}
