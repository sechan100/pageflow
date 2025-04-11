package org.pageflow.common.property;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

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
    Preconditions.checkNotNull(props, "ApplicationProperties가 초기화되지 않았습니다.");
    return props;
  }
}
