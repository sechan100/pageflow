package org.pageflow.common.result.code;

import org.pageflow.common.result.Result;

/**
 * <p>{@link Result}가 가지는 code 필드에 사용되는 enum의 스펙을 정의한다.
 * <hr>
 * <p>code: Enum.name()의 반환 값(enum 자체 문자열)</p>
 * <p>message: 에러에 대한 사람이 이해할 수 있는 간단한 설명.</p>
 * <p>
 *    dataType: 반환되는 데이터의 타입을 나타낸다. null일 경우 데이터를 반환하지 않는다.
 *    Collection이나 Map등 제네릭 타입을 사용하는 경우 반드시 DTO를 사용할 것.
 *    dataType이 Object인 경우는 null 데이터도 허용한다.
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
   * Collection이나 Map등을 반환하는 경우 반드시 DTO를 사용해야한다.
   */
  Class<?> getDataType();

  String name();
}
