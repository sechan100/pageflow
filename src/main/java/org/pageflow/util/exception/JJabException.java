package org.pageflow.util.exception;

/**
 * 굳이 예외 만들정도는 아니거나, 아직 안만들었는데 일단 RuntimeException을 쓰기는 좀 애매할 때 사용하는 짬처리 예외
 * @author : sechan
 */
public class JJabException extends RuntimeException { // 짬 예외
    public JJabException(String message, Throwable cause) {
        super(
                String.format("=== '%s'로 발생한 예외를 짬 때림 === \n %s", cause.getClass().getSimpleName(), message),
                cause
        );
    }
}
