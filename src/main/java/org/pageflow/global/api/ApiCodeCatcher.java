package org.pageflow.global.api;

import org.pageflow.global.api.code.ApiCode;
import org.pageflow.global.api.exception.ApiException;

import java.util.function.Consumer;

/**
 * 특정 apiCode 예외를 잡아낼 필요가 있는 경우 사용
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

        public CatcherBuilder doCatch(ApiCode apiCode, Consumer<ApiCode> consumer){
            if(e.getApiResponse().getCode() == apiCode.getCode()){
                consumer.accept(apiCode);
            }
            return this;
        }
    }
}
