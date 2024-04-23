package org.pageflow.boundedcontext.file.shared;

/**
 * @author : sechan
 */
public class FileProcessingException extends RuntimeException {

public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable e) {
        super(message, e);
    }

}
