package org.pageflow.boundedcontext.book.domain;

import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public final class Title extends SingleValueWrapper<String> {
    public Title(String value) {
        super(value);
        validate(value);
    }

    private static void validate(String value) {
        if(value == null || value.isEmpty()){
            throw Code4.EMPTY_VALUE.feedback("책의 제목이 없습니다.");
        }
    }
}
