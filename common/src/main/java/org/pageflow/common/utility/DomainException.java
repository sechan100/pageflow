package org.pageflow.common.utility;

/**
 * @author : sechan
 */
public abstract class DomainException extends RuntimeException {
  public DomainException() {
    super();
  }

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }

  public DomainException(Throwable cause) {
    super(cause);
  }
}
