package org.pageflow.common.transaction;

/**
 * @author : sechan
 */
public interface TransactionalCacheManager {
  void cachify(String key, Object value);

  Object get(String key);
}
