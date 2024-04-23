package org.pageflow.boundedcontext.user.port.out;

import org.pageflow.boundedcontext.user.domain.Penname;

/**
 * @author : sechan
 */
public interface PennameForbiddenWordPort {
    void validateForbiddenWord(Penname penname);
}
