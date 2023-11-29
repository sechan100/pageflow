package org.pageflow.base.exception.controller;

import org.pageflow.base.exception.nosuchentity.ApiNoSuchEntityException;
import org.pageflow.base.exception.entityaccessdenied.ApiEntityAccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * @author : sechan
 */
@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(ApiNoSuchEntityException.class)
    public ResponseEntity<Map<String, Object>> HandleApiNoSuchEntityException(ApiNoSuchEntityException e) {
        
        // 오류 응답을 생성
        return new ResponseEntity<>(
                Map.of(
                "message", e.getMessage(),
                "entityName", e.getEntityClass().getSimpleName()
                ),
                null,
                404
        );
    }
    
    @ExceptionHandler(ApiEntityAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> HandleEntityAccessDeniedException(ApiEntityAccessDeniedException e) {
        return new ResponseEntity<>(
                Map.of(
                        "message", "접근 권한이 없습니다."
                ),
                null,
                403
        );
    }
    
    
    
    
}
