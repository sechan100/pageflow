package org.pageflow.common.shared.jpa.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.shared.jpa.repository.TemporaryEntityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TemporaryEntityCleanupScheduler {
  private final TemporaryEntityRepository repository;


  /**
   * 1분마다 실행
   */
  @Scheduled(fixedRate = 60 * 1000)
  public void cleanup() {
    int deletedNum = repository.deleteAllByExpiredAtBefore(System.currentTimeMillis());
    log.info("Deleted [{}] temporary entities", deletedNum);
  }


}
