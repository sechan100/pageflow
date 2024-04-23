package org.pageflow.boundedcontext.user.domain;

import org.pageflow.shared.type.SingleValueWrapper;

/**
 * @author : sechan
 */
public class ProfileImage extends SingleValueWrapper<String> {

    private ProfileImage(String value) {
        super(value);
    }

    public static ProfileImage of(String value) {
        return new ProfileImage(value);
    }

    public boolean isExternalImage() {
        return !value.startsWith("/"); // 외부 이미지는 외부서버 도메인으로 시작함 (not URI)
    }

}
