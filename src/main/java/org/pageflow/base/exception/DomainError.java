package org.pageflow.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pageflow.domain.user.constants.UserSignupPolicy;
import org.springframework.http.HttpStatus;

/**
 * @author : sechan
 */
public class DomainError {
    
    @Getter
    @AllArgsConstructor
    public enum Common implements ErrorCode {
        DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "요청받은 데이터를 찾을 수 없습니다."),
        INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
        METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다."),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
        INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "입력값의 타입이 올바르지 않습니다."),
        HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
        
        ;
        private final HttpStatus httpStatus;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum File implements ErrorCode {
        
        // 유효하지 않은 값
        INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 이름입니다."),
        INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자입니다."),
        INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 경로입니다."),
        
        // 동작 실패
        FAIL_TO_UPLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
        FAIL_TO_DELETE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
        
        
        ;
        private final HttpStatus httpStatus;
        private final String message;
    }
    
    
    @Getter
    @AllArgsConstructor
    public enum User implements ErrorCode {
        
        // 빈 문자열
        BLANK_USERNAME(HttpStatus.BAD_REQUEST, "아이디를 입력해주세요."),
        BLANK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
        BLANK_EMAIL(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요."),
        BLANK_PENNAME(HttpStatus.BAD_REQUEST, "필명을 입력해주세요."),
        
        // 정규식 불일치
        USERNAME_REGEX_NOT_MATCH(HttpStatus.BAD_REQUEST, UserSignupPolicy.USERNAME_REGEX_DISCRIPTION),
        PASSWORD_REGEX_NOT_MATCH(HttpStatus.BAD_REQUEST, UserSignupPolicy.PASSWORD_REGEX_DISCRIPTION),
        EMAIL_REGEX_NOT_MATCH(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
        PENNAME_REGEX_NOT_MATCH(HttpStatus.BAD_REQUEST, UserSignupPolicy.PENNAME_REGEX_DISCRIPTION),
        
        // 벤
        UNUSEABLE_USERNAME(HttpStatus.BAD_REQUEST, "사용할 수 없는 아이디입니다."),
        UNUSEABLE_PENNAME(HttpStatus.BAD_REQUEST, "사용할 수 없는 필명입니다."),
        
        // 중복
        DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 아이디입니다."),
        DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
        DUPLICATE_PENNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 필명입니다."),
        
        // 불일치
        PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다."),
        PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
        
        // 찾지 못함
        USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
        USERNAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 아이디입니다."),
        
        ;private final HttpStatus httpStatus;
        private final String message;
    }
    
    
    
    
    public interface ErrorCode {
        HttpStatus getHttpStatus();
        String getMessage();
    }
}
