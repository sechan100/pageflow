package org.pageflow.boundedcontext.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public class UniqueFieldDuplicatedException extends DomainException {
    private final String field;
    private final String duplicatedValue;
}
