package org.pageflow.boundedcontext.user.domain.exception;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.common.exception.DomainException;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
public class UniqueFieldDuplicatedException extends DomainException {
    private final String field;
    private final String duplicatedValue;
}
