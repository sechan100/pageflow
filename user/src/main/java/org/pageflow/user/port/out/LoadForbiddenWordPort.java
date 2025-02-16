package org.pageflow.user.port.out;

import java.util.Collection;

/**
 * @author : sechan
 */
public interface LoadForbiddenWordPort {
  Collection<String> loadPennameForbiddenWords();
}
