package org.pageflow.core.init.module;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.CommonModule;
import org.pageflow.email.EmailModule;
import org.pageflow.file.FileModule;
import org.pageflow.user.UserModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 다른 모듈에 있는 root @Configuration 클래스를 @Import하는 설정 클래스.
 * 이걸 하지 않으면 BOOT-INF/lib에 jar로 들어가는 다른 모듈들의 Bean들은 스캔이 되지 않는다.
 * @author : sechan
 */
@Import({
  CommonModule.class,
  EmailModule.class,
  FileModule.class,
  UserModule.class,
})
@Configuration
@Slf4j
public class ModuleBeanScanConfigImportConfiguration {
  public ModuleBeanScanConfigImportConfiguration() {
    log.info("필요한 모듈의 Configuration 클래스를 Import합니다. Bean 스캔 시작");
  }
}
