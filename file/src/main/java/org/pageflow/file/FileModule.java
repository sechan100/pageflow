package org.pageflow.file;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.module.ApplicationModule;

/**
 * @author : sechan
 */
@Slf4j
@ApplicationModule
public class FileModule {
  public FileModule() {
    log.info("FileModule이 자동구성으로 로드되었습니다.");
  }
}
