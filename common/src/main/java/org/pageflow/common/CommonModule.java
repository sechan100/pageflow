package org.pageflow.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@Slf4j
@ConfigurationPropertiesScan
@SpringBootApplication
public class CommonModule {
    public CommonModule() {
        log.info("CommonModule이 자동구성으로 로드되었습니다.");
    }
}
