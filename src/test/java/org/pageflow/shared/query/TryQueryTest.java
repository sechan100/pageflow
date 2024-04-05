package org.pageflow.shared.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pageflow.boundedcontext.user.entity.SignupCache;
import org.pageflow.global.entity.Entity;
import org.pageflow.shared.JJamException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sechan
 */
class TryQueryTest {
    
    @Test
    void find() {
        // 성공 쿼리는 null이 아닌 값을 반환
        assertNotNull(
            createSuccessTryQuery().find()
        );
        
        // 빈 쿼리는 EmptyResultDataAccessException을 던짐
        assertThrows(
            EmptyResultDataAccessException.class,
            () -> createEmptyTryQuery().find()
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(
            RuntimeException.class,
            () -> createFailTryQuery().find()
        );
    }
    
    @Test
    void findOrElse() {
        Entity fallback = SignupCache.builder().username("dfd").build();
        Supplier<Entity> fallbackSupplier = () -> fallback;
        
        // 성공 쿼리는 null이 아닌 값을 반환
        assertNotNull(
            createSuccessTryQuery().findOrElse(fallbackSupplier)
        );
        
        // 빈 쿼리는 기본값을 반환
        assertEquals(
            fallback,
            createEmptyTryQuery().findOrElse(fallbackSupplier)
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(
            RuntimeException.class,
            () -> createFailTryQuery().findOrElse(fallbackSupplier)
        );
    }
    
    @Test
    void findOrElseThrow() {
        Supplier<RuntimeException> exceptionSupplier = () -> new JJamException("짬이 없어요!", new RuntimeException());
        
        // 성공 쿼리는 null이 아닌 값을 반환
        assertNotNull(
            createSuccessTryQuery().findOrElseThrow(exceptionSupplier)
        );
        
        // 빈 쿼리는 RuntimeException을 던짐
        assertThrows(
            JJamException.class,
            () -> createEmptyTryQuery().findOrElseThrow(exceptionSupplier)
        );
        
        // 실패 쿼리는 RuntimeException을 던짐
        assertThrows(
            RuntimeException.class,
            () -> createFailTryQuery().findOrElseThrow(exceptionSupplier)
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
    
    private <T extends Entity> TryQuery<T> createSuccessTryQuery() {
        return (TryQuery<T>) TryQuery.of(() -> SignupCache.builder().username("dfd").build());
    }
    
    private <T extends Entity> TryQuery<T> createEmptyTryQuery() {
        return TryQuery.of(() -> {
            throw new EmptyResultDataAccessException(1);
        });
    }
    
    private <T extends Entity> TryQuery<T> createFailTryQuery() {
        return TryQuery.of(() -> {
            throw new RuntimeException();
        });
    }
    
}