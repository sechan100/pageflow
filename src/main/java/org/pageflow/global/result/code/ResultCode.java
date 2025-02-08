package org.pageflow.global.result.code;

import java.util.Collections;

/**
 * <p>Result 객체는 code, title, message, data를 가진다.
 * 이 때, data를 제외한 나머지 3가지 필드는 ENUM에서 정의된다.</p>
 * <hr>
 * <p>code: ${10^3w + 10^2x + 10^1y + 10^0z}의 형태로 정의된다.</p>
 * <p>code는 (1000 <= code <= 9999)을 만족하는 정수이다.</p>
 * <p>title: Enum.name()으로 사용</p>
 * <p>message: 에러에 대한 사람이 이해할 수 있는 설명; 개발자를 대상으로 작성된다.</p>
 * <hr>
 * <p>apiCode는 자리별로 의미를 가지는데, 다음과 같다.</p>
 * <ul>
 *     <li>
 *         10^3: code 유형의 구분.
 *         http 상태 코드와의 혼동을 피하기 위해서 level별로 비슷한 의미를 가지도록 했지만, 완벽히 동일하지는 않다.
 *         <ul>
 *             <li>0XXX: 사용하지 않음</li>
 *             <li>1XXX: 인증, 인가 관련</li>
 *             <li>2XXX: 메타요청(요청 성공, 올바르지 않은 요청 등)</li>
 *             <li>3XXX: 리소스 관련 에러</li>
 *             <li>4XXX: 입력 유효성 검사 & 도메인 룰</li>
 *             <li>5XXX: 서버, 시스템 인프라 에러</li>
 *             <li>6XXX: 상태 변경 실패.</li>
 *             <li>7XXX: 미정</li>
 *             <li>8XXX: 미정</li>
 *             <li>9XXX: 미정</li>
 *         </ul>
 *     </li>
 *     <li>10^2, 10^1, 10^0: 내부에서 세부 범주별로 구분(Not Thorough)</li>
 *  </ul>
 *
 *  <p>
 *    dataType: 반환되는 데이터의 타입을 나타낸다. null일 경우 데이터를 반환하지 않는다.
 *    만약 Collection을 반환하는 경우,
 *  </p>
 *
 * @author : sechan
 */
public interface ResultCode {
  int getCode();

  default String getTitle() {
    return this.name();
  }

  String getMessage();

  /**
   * dataType은 Result 객체가 가지는 타입을 명시한다.
   * 만약 해당 타입이 Collection인 경우, DataType에는 Collection에 담기는 제네릭 타입을 명시하고 getCollectionType()을 통해 Collection 타입을 명시한다.
   */
  Class<?> getDataType();
  Class<? extends Collections> getCollectionType();

  String name();
}
