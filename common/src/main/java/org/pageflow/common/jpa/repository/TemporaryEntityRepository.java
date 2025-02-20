package org.pageflow.common.jpa.repository;

import org.pageflow.common.jpa.TemporaryEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : sechan
 */
public interface TemporaryEntityRepository extends BaseJpaRepository<TemporaryEntity, String> {

  @Query("""
      delete from #{#entityName} e
      where e.expiredAt < :currentTimeMillis
  """)
  @Modifying
  int deleteAllByExpiredAtBefore(@Param("currentTimeMillis") long currentTimeMillis);
}
