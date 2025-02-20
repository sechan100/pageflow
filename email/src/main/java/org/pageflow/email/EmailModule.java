package org.pageflow.email;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.module.ApplicationModule;

/**
 * @author : sechan
 */
@Slf4j
@ApplicationModule
public class EmailModule {
  public EmailModule() {
    log.info("EmailModule이 자동구성으로 로드되었습니다.");
  }
}
