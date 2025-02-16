package org.pageflow.common.property;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author : sechan
 */
@Component
public class PropsAware {

  private static ApplicationProperties props;

  public PropsAware(ApplicationProperties props) {
    PropsAware.props = props;
  }

  @SuppressWarnings("StaticVariableUsedBeforeInitialization")
  public static ApplicationProperties use() {
    Assert.notNull(props, "AppProps가 초기화되지 않았습니다.");
    return props;
  }
}
