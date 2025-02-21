package org.pageflow.book;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.module.ApplicationModule;

/**
 * @author : sechan
 */
@Slf4j
@ApplicationModule
public class BookModule {
  public BookModule() {
    log.info("BookModule이 자동구성으로 로드되었습니다.");
  }
}
