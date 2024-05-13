package org.pageflow.boundedcontext.book.domain;

import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.util.StringUtils;

/**
 * @author : sechan
 */
public final class Title extends SingleValueWrapper<String> {
    public Title(String value) {
        super(value);
        validate(value);
    }

    private static void validate(String value) {
        if(!StringUtils.hasText(value)){
            throw InputValueException.builder()
                .message("제목을 입력해주세요.")
                .field("title", value)
                .build();
        }
    }
}
