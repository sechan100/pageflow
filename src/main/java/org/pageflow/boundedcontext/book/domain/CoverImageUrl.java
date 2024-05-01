package org.pageflow.boundedcontext.book.domain;

import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public final class CoverImageUrl extends SingleValueWrapper<String> {
//    private static final String DEFAULT_COVER_IMAGE_URL = PropsAware.use();

    public CoverImageUrl(String value) {
        super(value);
        validate(value);
    }

    private static void validate(String value) {
        if(value == null || value.isEmpty()){
            throw Code4.EMPTY_VALUE.feedback("커버 이미지 url이 없습니다.");
        }
    }
}
