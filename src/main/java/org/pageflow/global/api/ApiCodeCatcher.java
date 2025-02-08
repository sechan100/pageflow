package org.pageflow.global.api;

import org.pageflow.global.result.code.ResultCode;
import org.pageflow.global.result.exception.ApiException;

import java.util.function.Consumer;

/**
 * 특정 apiCode 예외를 잡아낼 필요가 있는 경우 사용
 *
 * @author : sechan
 */
public abstract class ApiCodeCatcher {
  public static CatcherBuilder apiException(ApiException apiException) {
    return new CatcherBuilder(apiException);
  }


  public static class CatcherBuilder {
    private final ApiException e;

    private CatcherBuilder(ApiException e) {
      this.e = e;
    }

    public CatcherBuilder doCatch(ResultCode resultCode, Consumer<ResultCode> consumer) {
      if(e.getApiResponse().getCode()==resultCode.getCode()){
        consumer.accept(resultCode);
      }
      return this;
    }
  }
}
