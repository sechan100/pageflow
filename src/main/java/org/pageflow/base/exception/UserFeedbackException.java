package org.pageflow.base.exception;

import lombok.Getter;
import org.pageflow.base.exception.code.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserFeedbackException extends RuntimeException {
    
    @Getter
    private final ErrorCode errorCode;
    private final String messageWithArgs;
    
    public UserFeedbackException(ErrorCode domainError, String... args) {
        super();
        this.errorCode = domainError;
        Pattern pattern = Pattern.compile("\\{\\d}");
        Matcher matcher = pattern.matcher(errorCode.getMessageTemplate());
        
        // 파라미터 갯수 확인 '{0과 양의 정수}'의 형태만 찾을 수 있음
        int argCount = 0;
        while (matcher.find()) {
            argCount++;
        }
        // 파라미터 갯수 불일치
        if(argCount != args.length){
            throw new ErrorCodeParseException(errorCode + "에러 코드를 담은 exception을 생성할 수 없습니다: 파라미터 개수" + argCount + "개 중 " + args.length + "개가 입력되었습니다.");
        }
        
        // 파라미터 치환
        String message = errorCode.getMessageTemplate();
        for(int i = 0; i < argCount; i++){
            message = message.replaceAll("\\{"+(i)+"}", args[i]);
        }
        
        // {0과 양의 정수}의 형태가 남아있다면 제대로 파싱되지 않은 것이다.
        if(message.matches(".*\\{\\d}.*")){
            throw new ErrorCodeParseException("에러코드 메세지 파싱 실패: 아직 남아있는 파라미터가 존재하거나, {0과 양의 정수}의 형태가 존재 할 수 있습니다.'" + message + "'");
        }
        
        this.messageWithArgs = message;
    }
    
    
    @Override
    public String getMessage() {
        return messageWithArgs;
    }
    
}
