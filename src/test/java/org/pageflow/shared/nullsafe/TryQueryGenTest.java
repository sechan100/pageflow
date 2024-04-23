package org.pageflow.shared.nullsafe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pageflow.boundedcontext.user.adapter.out.cache.entity.OAuth2PreSignupCache;
import org.pageflow.shared.jpa.JpaEntity;
import org.pageflow.shared.utility.JJamException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sechan
 */
class TryQueryGenTest {
    
    @Test
    void find() {
        // 성공 쿼리는 null이 아닌 값을 반환
        assertNotNull(
            createSuccessTryQuery().query()
        );
        
        // 빈 쿼리는 EmptyResultDataAccessException을 던짐
        assertThrows(
            EmptyResultDataAccessException.class,
            () -> createEmptyTryQuery().query()
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(
            RuntimeException.class,
            () -> createFailTryQuery().query()
        );
    }
    
    @Test
    void findOrElse() {
        JpaEntity fallback = OAuth2PreSignupCache.builder().username("dfd").build();
        Supplier<JpaEntity> fallbackSupplier = () -> fallback;
        
        // 성공 쿼리는 null이 아닌 값을 반환
        assertNotNull(
            createSuccessTryQuery().queryOrElse(fallbackSupplier)
        );
        
        // 빈 쿼리는 기본값을 반환
        assertEquals(
            fallback,
            createEmptyTryQuery().queryOrElse(fallbackSupplier)
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(
            RuntimeException.class,
            () -> createFailTryQuery().queryOrElse(fallbackSupplier)
        );
    }
    
    @Test
    void findOrElseThrow() {
        Supplier<RuntimeException> exceptionSupplier = () -> new JJamException("짬이 없어요!", new RuntimeException());
        
        // 성공 쿼리는 null이 아닌 값을 반환
        assertNotNull(
            createSuccessTryQuery().queryOrElseThrow(exceptionSupplier)
        );
        
        // 빈 쿼리는 RuntimeException을 던짐
        assertThrows(
            JJamException.class,
            () -> createEmptyTryQuery().queryOrElseThrow(exceptionSupplier)
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(
            RuntimeException.class,
            () -> createFailTryQuery().queryOrElseThrow(exceptionSupplier)
        );
    }
    
    @Test
    void onFind() {
        createSuccessTryQuery().onFind(Assertions::assertNotNull);
        
        createEmptyTryQuery().onFind(
            (entity) -> fail("빈 쿼리에 대한 onFind 콜백이 호출되었습니다. 잘못된 호출임")
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(RuntimeException.class,
            () -> createFailTryQuery().onFind(Assertions::assertNotNull)
        );
    }
    
    @Test
    void onNotFound() {
        createSuccessTryQuery().onNotFound(
            () -> fail("성공 쿼리에 대한 onNotFound 콜백이 호출되었습니다. 잘못된 호출임")
        );
        
        createEmptyTryQuery().onNotFound(
            () -> System.out.println("빈 쿼리에 대한 onNotFound 콜백이 호출되었습니다. 성공적")
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(RuntimeException.class,
            () -> createFailTryQuery().onNotFound(
                () -> fail("실패 쿼리에 대한 onNotFound 콜백이 호출되었습니다. 잘못된 호출임")
            )
        );
    }
    
    private <T extends JpaEntity> QueryRecover<T> createSuccessTryQuery() {
        return (QueryRecover<T>) QueryRecover.of(() -> OAuth2PreSignupCache.builder().username("dfd").build());
    }
    
    private <T extends JpaEntity> QueryRecover<T> createEmptyTryQuery() {
        return QueryRecover.of(() -> {
            throw new EmptyResultDataAccessException(1);
        });
    }
    
    private <T extends JpaEntity> QueryRecover<T> createFailTryQuery() {
        return QueryRecover.of(() -> {
            throw new RuntimeException();
        });
    }
    
}