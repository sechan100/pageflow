package org.pageflow.user.port.out;

import org.pageflow.common.user.UID;
import org.pageflow.user.dto.SessionUserDto;

/**
 * @author : sechan
 */
public interface LoadSessionUserPort {
  SessionUserDto load(UID uid);
}
