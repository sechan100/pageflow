package org.pageflow.global.api.code;

/**
 * <p>Api의 응답은 code, title, message, feedbak, data를 가진다.
 * 이 때, data를 제외한 나머지 4가지 정보는 하나의 ENUM으로 정의된다.</p>
 * <hr>
 * <p>code: ${(+-)10^3w + 10^2x + 10^1y + 10^0z}의 형태로 정의된다.</p>
 * <p>code는 (|1000| <= code <= |9999|)을 만족하는 정수이다.</p>
 * <p>Exposable Code: 0보다 큰 code로 표현된다. 사용자에게 그대로 노출될 코드이다.
 * Unexposable code: 0보다 작은 code는 모종의 이유(주로 보안문제)로 사용자에게 그대로 노출되어서는 안되는 코드를 의미한다. 음수 code는 외부로 노출되서는 안되기에, 주로 내부적으로 log를 남기고 사용자에게는 다른 code로 변환하여 응답하는식으로 처리한다.</p>
 * <p>title: Enum.name()으로 사용</p>
 * <p>message: 에러에 대한 사람이 이해할 수 있는 설명; 개발자를 대상으로 작성된다.</p>
 * <p>feedback: 사용자에게 보여줄 오류 피드백 메세지. 기본값은 지정되어있지만, 언제든지 구체적인 정보를 포함하는 피드백으로 치환될 수 있다.</p>
 * <hr>
 * <p>apiCode는 자리별로 의미를 가지는데, 다음과 같다.</p>
 * <ul>
 *     <li>
 *         10^3: code 유형의 구분. 동일 domain 내에서는 중복되지 않는다.
 *         http 상태 코드와의 혼동을 피하기 위해서 level별로 비슷한 의미를 가지도록 했지만, 완벽히 동일하지는 않다.
 *         <ul>
 *             <li>0XXX: 사용하지 않음</li>
 *             <li>1XXX: 인증, 인가 관련</li>
 *             <li>2XXX: 메타요청(요청 성공, 올바르지 않은 요청 등)</li>
 *             <li>3XXX: 리소스 관련 에러</li>
 *             <li>4XXX: 사용자 입력 필드 유효성 에러</li>
 *             <li>5XXX: 서버, 시스템 인프라 에러</li>
 *             <li>6XXX: 상태 변경 실패.</li>
 *             <li>7XXX: 미정</li>
 *             <li>8XXX: 미정</li>
 *             <li>9XXX: 미정</li>
 *         </ul>
 *     </li>
 *     <li>10^2, 10^1, 10^0: 내부에서 세부 범주별로 구분(Not Thorough)</li>
 *  </ul>
 * @author : sechan
 */
public interface ApiCode {
    int getCode();
    default String getTitle() {
        return this.name();
    }
    String getMessage();
    String getFeedback();

    /**
     * Unexposable Code의 경우, 이 메소드를 통해서 해당 값을 대체할 ApiCode를 반환한다. .
     * @apiNote 음수 code를 가진 ApiCode는 반드시 해당 메소드를 Override하여야한다.
     */
    default ApiCode substituteForRedacted(){
        return this;
    }

    String name();
}
