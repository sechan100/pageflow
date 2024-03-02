package org.pageflow.boundedcontext.user.constants;

/**
 * Profile 엔티티 조회시, EAGER로 즉시 초기화시킬 참조의 깊이를 나타냄
 * @author : sechan
 */
public enum UserFetchDepth {
    /**
     * 아무 것도 조회하지 않음. Account와 Profile을 모두 PROXY로 초기화한다.
     */
    PROXY,
    
    /**
     * Profile 엔티티만을 즉시 초기화한다.
     */
    PROFILE,
    
    /**
     * Account 만을 즉시 초기화한다.
     */
    ACCOUNT,
    
    /**
     * Account와 Profile을 모두 즉시 초기화한다.
     */
    FULL

}
