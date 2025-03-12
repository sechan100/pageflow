package org.pageflow.common.result;


/**
 * {@link Result}의 데이터가 null인 경우 제네릭 타입으로 사용하는 객체.
 * ResultCode의 dataType이 Object인 경우 null 데이터를 허용한다.
 * @author : sechan
 */
@SuppressWarnings("InstantiationOfUtilityClass")
public class NullData {
  private static final NullData INSTANCE = new NullData();

  private NullData() {
  }

  public static NullData getInstance() {
    return INSTANCE;
  }
}