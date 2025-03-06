package org.pageflow.core.controller;

import lombok.Value;
import org.pageflow.common.property.ApplicationProperties;

/**
 * @author : sechan
 */
@Value
public class ServerPropertiesRes {
  int refreshTokenExpireDays;

  public ServerPropertiesRes(ApplicationProperties properties) {
    this.refreshTokenExpireDays = properties.auth.refreshTokenExpireDays;
  }
}

