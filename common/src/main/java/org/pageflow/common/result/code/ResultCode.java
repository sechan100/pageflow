package org.pageflow.common.result.code;

import org.pageflow.common.result.Result;

import java.util.Collection;

/**
 * <p>{@link Result}가 가지는 code 필드에 사용되는 enum의 스펙을 정의한다.
 * <hr>
 * <p>code: Enum.name()의 반환 값(enum 자체 문자열)</p>
 * <p>message: 에러에 대한 사람이 이해할 수 있는 간단한 설명.</p>
 * <p>
 *    dataType: 반환되는 데이터의 타입을 나타낸다. null일 경우 데이터를 반환하지 않는다.
 *    만약 Collection을 반환하는 경우,
 * </p>
 *
 * @author : sechan
 */
public interface ResultCode {

  default String getCode() {
    return this.name();
  }

  String getMessage();

  /**
   * dataType은 Result 객체가 가지는 타입을 명시한다.
   * 만약 해당 타입이 Collection인 경우, DataType에는 Collection에 담기는 제네릭 타입을 명시하고 getCollectionType()을 통해 Collection 타입을 명시한다.
   */
  Class<?> getDataType();
  Class<? extends Collection> getCollectionType();

  String name();
}
