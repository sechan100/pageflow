package org.pageflow.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.global.response.vo.FeedbackVo;

import java.util.Map;

/**
 * API 공통 응답 객체의 가장 큰 범주의 상태를 나타낸다.<br>
 * {@link GeneralResponse}를 구성할 때에, data의 타입으로 반드시 availableDataType, 또는 그의 구현을 사용해야한다.<br>
 * 한편, Map 또는 Collection을 사용하는 경우는, value로 원시(primitive) 값 타입의 Wrapper 클래스만을 허용하는 것으로 한다.<br><br>
 * 200대: 성공<br>
 * 400대: 피드백이 필요한 에러<br>
 * 500대: 서버 에러(사용자의 피드백이 필요하지 않음)<br>
 * @author : sechan
 */
@AllArgsConstructor
@Getter
public enum ApiStatus {
    
    // 성공
      SUCCESS(200, "성공", Object.class)
    // 요청이 성공했지만, 정확히 기대하는 응답이 아닌 경우(ex: OAuth2 로그인을 시도했지만 회원가입 창으로 이동한 경우)
    , CONDITIONAL_SUCCESS(201, "요청 분기 후 성공", Object.class)
    
    
    // 피드백(사용자의 피드백에 필요함)
    , FEEDBACK(400,"사용자 피드백이 필요합니다.", FeedbackVo.class)
    // Spring bean validation으로 인해 발생한 필드 검증 에러
    , FIELD_VALIDATION_ERROR(401, "필드 검증 에러", Map.class)
    
    
    // 서버 에러(사용자에게 에러에 관한 구체적 정보를 전달하지 않음)
    , ERROR(500, "에러가 발생했습니다.", null)
    // ApiStatus 처리중, 허용 불가능한 데이터로 인해 처리할 수 없는 경우
    , INAVAILABLE_DATA_TYPE(501, "ApiStatus에 허용되지 않는 데이터 타입입니다.", String.class)
    
    
    ;
    public static final String ATTRIBUTE_KEY = "apiStatus";
    private final int code;
    private final String message;
    private final Class<?> availableDataType;
}
