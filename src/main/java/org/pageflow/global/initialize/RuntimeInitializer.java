package org.pageflow.global.initialize;

/**
 * <p>해당 인터페이스의 구현체는 runtime 초기화 시점에 {@link InitializerManager}에 의해 로드되어, 초기화 작업을 수행한다.</p>
 * @author : sechan
 */
public interface RuntimeInitializer {
    void initialize() throws Throwable;

    default boolean isActivated() {
        return true;
    }
}
