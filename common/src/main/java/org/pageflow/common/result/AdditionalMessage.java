package org.pageflow.common.result;

import lombok.Data;

/**
 * 추가적인 message를 제공하는 데이터 타입. 그냥 String은 혼란의 여지가 있으므로 해당 클래스를 사용한다.
 * @author : sechan
 */
@Data
public class AdditionalMessage {
  private final String additionalMessage;

  public static AdditionalMessage of(String message) {
    return new AdditionalMessage(message);
  }
}
