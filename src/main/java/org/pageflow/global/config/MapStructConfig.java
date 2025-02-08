package org.pageflow.global.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.ERROR
)
public class MapStructConfig {
}