package org.pageflow.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : sechan
 */
@Slf4j
@SpringBootApplication
public class FileModule {
  public FileModule() {
    log.info("FileModule이 자동구성으로 로드되었습니다.");
  }
}
