package org.pageflow.base.exception.code;

/**
 * {@link org.pageflow.base.exception.code.ApiStatusCode } 상태중, FEEDBACK 상태의 세부 상태코드
 * @author : sechan
 */
public interface FeedbackCode {
    
    /**
    * {0}과 같은 형태를 사용해서 메세지의 템플릿을 정의할 수 있다.
    * {@link java.text.MessageFormat} 참고
    * */
    String getMessageTemplate();
}
