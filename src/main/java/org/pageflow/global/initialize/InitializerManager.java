package org.pageflow.global.initialize;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitializerManager {
    private final List<RuntimeInitializer> initializers;
    
    @Bean
    public ApplicationRunner runner() {
        return args -> {
            for(RuntimeInitializer initializer : initializers) {
                try {
                    if(initializer.isActivated()) {
                        initializer.initialize();
                    }
                } catch (Throwable e) {
                    log.error("Runtime Initializer Error가 발생했습니다.");
                    throw new RuntimeInitializeException(e);
                }
            }
        };
    }
    
}