package org.pageflow.boundedcontext.user.domain;

import org.pageflow.global.api.code.Code4;
import org.pageflow.global.property.PropsAware;
import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public class ProfileImageUrl extends SingleValueWrapper<String> {

    private static final String INTERNAL_IMAGE_PREFIX = PropsAware.use().file.webBaseUrl;

    private ProfileImageUrl(String value) {
        super(value);
    }

    public static ProfileImageUrl of(String value) {
        if(value == null || value.isEmpty()){
            throw Code4.EMPTY_VALUE.feedback("프로필 이미지 url이 없습니다.");
        }
        return new ProfileImageUrl(value);
    }

    public boolean isExternalImage() {
        return !isInternalImage();
    }

    public boolean isInternalImage() {
        return value.startsWith(INTERNAL_IMAGE_PREFIX);
    }

}
