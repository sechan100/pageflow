package org.pageflow.user.port.out;

import org.pageflow.common.user.UID;
import org.pageflow.user.dto.SessionUserDto;

/**
 * @author : sechan
 */
public interface LoadSessionUserPort {

  /**
   * 세션 사용자 정보를 조회한다.
   * @param uid ANONYMOUS 사용자의 UID가 들어온 경우 예외
   * @return
   */
  SessionUserDto load(UID uid);
}
