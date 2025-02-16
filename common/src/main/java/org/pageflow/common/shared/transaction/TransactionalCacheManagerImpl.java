package org.pageflow.common.shared.transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : sechan
 */
@Component
public class TransactionalCacheManagerImpl implements TransactionalCacheManager, TransactionSynchronization {
  private static final ThreadLocal<Map<String, Object>> cacheThreadLocal = new ThreadLocal<>();
  private static final ThreadLocal<Boolean> isSynchronizationActive = new ThreadLocal<>();


  public TransactionalCacheManagerImpl() {
    isSynchronizationActive.set(Boolean.FALSE);
  }

  @Override
  public void cachify(String key, Object value) {
    Map<String, Object> cache = cacheThreadLocal.get();
    if(cache==null){
      cache = new HashMap<>();
      cacheThreadLocal.set(cache);
    }
    cache.put(key, value);

    if(!isSynchronizationActive.get()){
      TransactionSynchronizationManager.registerSynchronization(this);
      isSynchronizationActive.set(Boolean.TRUE);
    }
  }

  @Override
  public Object get(String key) {
    Map<String, Object> cache = cacheThreadLocal.get();
    if(cache==null){
      return null;
    }
    return cache.get(key);
  }


  @Override
  public void afterCompletion(int status) {
    cacheThreadLocal.remove();
    isSynchronizationActive.set(Boolean.FALSE);
  }
}
