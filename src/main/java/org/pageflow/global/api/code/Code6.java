package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.global.api.ApiException;

/**
 * <p>LEVEL: 6000</p>
 * <p>
 *     멱등적이지 않은 요청이나, 서버의 상태를 변경하는 로직에서 에러가 발생한 경우들을 다룬다.
 * </p>
 *
 *
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum Code6 implements ApiCode {



    // ######################
    ;
    private final int code;
    private final String message;
    private final String feedback;

    public ApiException fire() {
        return new ApiException(this, this.getFeedback(), null);
    }
}
