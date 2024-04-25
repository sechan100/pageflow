package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.global.api.FeedbackTemplate;

import java.util.function.Function;

/**
 * <p>LEVEL: 3000</p>
 * <p>
 *     요청된 리소스에 문제가 있는 경우
 * </p>
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum Code3 implements ApiCode {
    // 3000: 데이터베이스
      DATA_NOT_FOUND(3000, "요청된 데이터를 찾을 수 없음", "데이터를 찾을 수 없습니다.")

    // 3400: 파일
    , FILE_NOT_FOUND(3400, "요청된 파일을 찾을 수 없음", "파일을 찾을 수 없습니다.")





    // ######################
    ;
    private final int code;
    private final String message;
    private final String feedback;

    public ApiException feedback(Function<FeedbackTemplate, String> feedbackSupplier) {
        return feedback(feedbackSupplier.apply(FeedbackTemplate.getINSTANCE()));
    }

    public ApiException feedback(String feedback) {
        return new ApiException(this, feedback, null);
    }
}
