package org.pageflow.boundedcontext.common.exception;

import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class UniqueFieldDuplicatedException extends DomainException {
    private final String field;
    private final String duplicatedValue;

    public UniqueFieldDuplicatedException(String field, String duplicatedValue, String message) {
        super(message);
        this.field = field;
        this.duplicatedValue = duplicatedValue;
    }
}
