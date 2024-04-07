package org.pageflow.boundedcontext.user.constants;

/**
 * Profile 엔티티 조회시, EAGER로 즉시 초기화시킬 참조의 깊이를 나타냄
 * @author : sechan
 */
public enum UserFetchDepth {
    PROFILE,
    ACCOUNT,
    FULL
}
