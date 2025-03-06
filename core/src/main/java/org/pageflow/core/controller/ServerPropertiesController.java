package org.pageflow.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.ApplicationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ServerPropertiesController {
  private final ApplicationProperties properties;

  @GetMapping("/server-properties")
  @Operation(summary = "서버 프로퍼티 조회")
  public ServerPropertiesRes getServerProperties() {
    return new ServerPropertiesRes(properties);
  }

}
