package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * <p>LEVEL: 4000</p>
 * <p>
 *     사용자의 입력값에 대한 유효성 검사 에러만을 다룬다.
 *     기본적으로 feedback을 default 값으로 가지고 있지만, 에러를 던지는 상황에 직접 다시 구성해줄 것을 권고한다.
 * </p>
 *
 * <p>다음은 대략적인 코드별 유형을 구분한 것이다.</p>
 * <ul>
 *     <li>4100: 기본적인 유효성 에러; null, 정규식, 길이 등</li>
 *     <li>4200: 특별한 규칙을 가진 필드들의 유효성 위반을 다룬다.(중복, 금지어 등)</li>
 * </ul>
 *
 * @author : sechan
 */
@Getter
@AllArgsConstructor
public enum Code4 implements ApiCode {

    // 4100: 기본적인 유효성 에러
      INVALID_FIELD(4100, "유효하지 않은 입력값", "올바른 값을 입력해주세요.")
    , MISSING_FIELD(4101, "요청에 필요한 필드가 Null 또는 Empty임.", "데이터를 모두 입력해주세요.")
    , FORMAT_MISMATCH(4110, "형식 불일치(정규식, 길이, etc.)", "올바른 값 형식이 아닙니다.")
    , VALUE_OUT_OF_RANGE(4120, "숫자형 필드에서 정해둔 범위를 벗어난 값이 입력됨", "올바른 범위의 값을 입력해주세요.")
    , INVALID_ENUM_VALUE(4130, "열거형에 존재하지 않는 값이 입력됨", "올바른 값을 입력해주세요.")

    // 4200: 도메인 요구사항
    , UNIQUE_FIELD_DUPLICATED(4200, "unique 칼럼 중복", "이미 존재하는 값입니다. 다른 값을 입력해주세요.")
    , CONTAINS_FORBIDDEN_WORD(4210, "금지어 포함", "사용할 수 없는 단어가 포함되어 있습니다. 다른 값을 입력해주세요.")

    // 4300: Spring Bean Validation 에러
    , FIELD_VALIDATION_FAIL(4300, "필드 유효성 검사 실패", "올바른 값을 입력해주세요.")

    // 4400: 필드 해석 실패
    , FIELD_PARSE_FAIL(4400, "필드 파싱 실패; 잘못된 형식", "입력 데이터 형식이 올바르지 않습니다.")


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
