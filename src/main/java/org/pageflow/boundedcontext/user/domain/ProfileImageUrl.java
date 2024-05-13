package org.pageflow.boundedcontext.user.domain;

import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.global.property.PropsAware;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.util.StringUtils;

/**
 * @author : sechan
 */
public class ProfileImageUrl extends SingleValueWrapper<String> {

    private static final String INTERNAL_IMAGE_PREFIX = PropsAware.use().file.webBaseUrl;

    private ProfileImageUrl(String value) {
        super(value);
    }

    public static ProfileImageUrl from(String value) {
        validate(value);
        return new ProfileImageUrl(value);
    }

    public boolean isExternalImage() {
        return !isInternalImage();
    }

    public boolean isInternalImage() {
        return value.startsWith(INTERNAL_IMAGE_PREFIX);
    }


    private static void validate(String value) {
        if(!StringUtils.hasText(value)){
            throw InputValueException.builder()
                .message("프로필 이미지 URL을 입력해주세요.")
                .field("profileImageUrl", value)
                .build();
        }
    }

}
