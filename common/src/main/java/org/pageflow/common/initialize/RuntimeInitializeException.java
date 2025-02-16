package org.pageflow.common.initialize;

/**
 * @author : sechan
 */
public class RuntimeInitializeException extends RuntimeException {
  public RuntimeInitializeException(Throwable cause) {
    super("Runtime Initialize Fail ", cause);
  }
}
