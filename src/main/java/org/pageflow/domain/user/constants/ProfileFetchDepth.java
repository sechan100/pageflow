package org.pageflow.domain.user.constants;

/**
 * Profile 엔티티 조회시, EAGER로 즉시 초기화시킬 참조의 깊이를 나타냄
 * @author : sechan
 */
public enum ProfileFetchDepth {
    /**
     * 아무 것도 조회하지 않음. Profile 엔티티의 JPA Proxy 객체만을 반환한다.
     */
    PROXY,
    /**
     * Profile 엔티티의 직속 필드만 모두 초기화. 나머지는 모두 LAZY로 초기화한다.
     */
    BASIC,
    /**
     * Profile 엔티티를 즉시 초기화하고, Account 객체까지 즉시 fetch join하여 한번에 초기화한다.
     */
    WITH_ACCOUNT,

}
