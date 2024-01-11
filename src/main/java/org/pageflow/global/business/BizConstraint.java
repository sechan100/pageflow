package org.pageflow.global.business;

/**
 * @author : sechan
 */
public interface BizConstraint {
    
    /**
    * {0}과 같은 형태를 사용해서 메세지의 템플릿을 정의할 수 있다.
    * {@link java.text.MessageFormat} 참고
    * */
    String getMessageTemplate();
}
