package org.pageflow.boundedcontext.book.domain;

import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.util.StringUtils;

/**
 * @author : sechan
 */
public final class CoverImageUrl extends SingleValueWrapper<String> {
//    private static final String DEFAULT_COVER_IMAGE_URL = PropsAware.use();

    private CoverImageUrl(String value) {
        super(value);
    }

    public static CoverImageUrl from(String value) {
        return new CoverImageUrl(value);
    }

    private static void validate(String value) {
        if(!StringUtils.hasText(value)){
            throw InputValueException.builder()
                .message("커버 이미지 URL을 입력해주세요.")
                .field("coverImageUrl", null)
                .build();
        }
    }
}
