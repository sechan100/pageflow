package org.pageflow.base.exception;

import lombok.Getter;
import org.pageflow.base.exception.code.FeedbackCode;

import java.text.MessageFormat;


public class UserFeedbackException extends RuntimeException {
    
    @Getter
    private final FeedbackCode feedbackCode;
    private final String message;
    
    public UserFeedbackException(FeedbackCode domainError, Object... args) {
        super();
        this.feedbackCode = domainError;
        
        MessageFormat messageFormat = new MessageFormat(domainError.getMessageTemplate());
        this.message = messageFormat.format(args);
    }
    
    
    @Override
    public String getMessage() {
        return message;
    }
    
}
