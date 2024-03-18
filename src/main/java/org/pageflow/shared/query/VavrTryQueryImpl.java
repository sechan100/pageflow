package org.pageflow.shared.query;

import io.vavr.control.Try;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author : sechan
 */

public class VavrTryQueryImpl<T> implements TryQuery<T> {
    
    private final Try<T> tryQuery;
    
    public VavrTryQueryImpl(Supplier<T> query) {
        this.tryQuery = Try.of(query::get);
    }
    
    @Override
    public T find() throws EmptyResultDataAccessException {
        return tryQuery.get();
    }
    
    @Override
    public T findOrElse(T defaultValue) {
        return this.findOrElse(() -> defaultValue);
    }
    
    @Override
    public T findOrElse(Supplier<T> defaultValueProvider) {
        return tryQuery.recover(EmptyResultDataAccessException.class, (x) -> defaultValueProvider.get()).get();
    }
    
    @Override
    public T findOrElseThrow(Supplier<RuntimeException> supplier) {
        return tryQuery.recover(EmptyResultDataAccessException.class, (x) -> {
            throw supplier.get();
        }).get();
    }
    
    @Override
    public TryQuery<T> onFind(Consumer onFindCallback) {
        if(tryQuery.isSuccess()) {
            onFindCallback.accept(tryQuery.get());
        } else {
            if(!(tryQuery.getCause() instanceof EmptyResultDataAccessException)) {
                throw (RuntimeException) tryQuery.getCause();
            }
        }
        return this;
    }
    
    @Override
    public TryQuery<T> onNotFound(Runnable onNotFoundCallback) {
        if(tryQuery.isSuccess()){
            return this;
        }
        
        if(tryQuery.getCause() instanceof EmptyResultDataAccessException) {
            onNotFoundCallback.run();
        } else {
            throw (RuntimeException) tryQuery.getCause();
        }
        return this;
    }
}
