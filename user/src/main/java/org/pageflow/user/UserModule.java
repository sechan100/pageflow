package org.pageflow.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : sechan
 */
@Slf4j
@SpringBootApplication
public class UserModule {
  public UserModule() {
    log.info("UserModule이 자동구성으로 로드되었습니다.");
  }
}
