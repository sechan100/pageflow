package org.pageflow.global.response;

/**
 * {@link GeneralResponse}에서 ApiStatus에 허용된 데이터 타입과 다른 데이터 타입이 들어온 경우 발생시킴
 * @author : sechan
 */
public class InavailableDataTypeException extends RuntimeException {
    public InavailableDataTypeException(String message) {
        super(message);
    }
}
