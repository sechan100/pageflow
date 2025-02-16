package org.pageflow.user.port.in;

import lombok.Value;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
public class IssueSessionCmd {
  /**
   * 별다른 인증절차는 진행하지 않기 때문에, 세션을 발급하려는 사용자가 인증된 이후에 호출하는 것인지 잘 확인할 것.
   */
  UID authenticatedAccountId;
}
