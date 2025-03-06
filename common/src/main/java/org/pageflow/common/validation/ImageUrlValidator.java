package org.pageflow.common.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.property.ApplicationProperties;
import org.springframework.stereotype.Service;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUrlValidator {
  private final ApplicationProperties applicationProperties;

  public boolean isExternalUrl(String url) {
    return !isInternalUrl(url);
  }

  public boolean isInternalUrl(String url) {
    return url.startsWith(applicationProperties.file.public_.webBaseUrl);
  }
}
