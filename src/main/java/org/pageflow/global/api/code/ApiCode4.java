package org.pageflow.global.api.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.pageflow.global.api.ResDataTypes.FieldName;
import static org.pageflow.global.api.ResDataTypes.FieldValidation;

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
public enum ApiCode4 implements ApiCode {
    // 4000: http 스펙
      MISSING_REQUEST_PARAMETER(4000, "필수 요청 파라미터 누락")
    , REQUIRED_COOKIE_NOT_FOUND(4001, "필수 쿠키 누락", FieldName.class)

    // 4100: 기본적인 유효성 에러
    , FIELD_VALIDATION_FAIL(4100, "필드 유효성 검사 실패", FieldValidation.class)

    // 4200: 범용 도메인 요구사항
    , BAD_CREDENTIALS(4210, "자격증명 실패", FieldName.class)

    // 4500: 구체 도메인 요구사항
    , UNIQUE_FIELD_DUPLICATED(4510, "unique 칼럼 중복")
    , CONTAINS_FORBIDDEN_WORD(4511, "금지어 포함")


    ;
    private final int code;
    private final String message;
    private final Class<?> dataType;

    ApiCode4(int code, String message) {
        this(code, message, null);
    }

}
