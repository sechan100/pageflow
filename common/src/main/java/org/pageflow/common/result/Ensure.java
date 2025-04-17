package org.pageflow.common.result;

import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
public abstract class Ensure {

  public static <T> void that(boolean condition, ResultCode code, T data) {
    if(!condition) {
      throw new ResultException(code, data);
    }
  }

  public static void that(boolean condition, ResultCode code) {
    Ensure.that(condition, code, null);
  }

  public static <T> void notNull(T object, ResultCode code, T data) {
    if(object == null) {
      throw new ResultException(code);
    }
  }

  public static <T> void notNull(T object, ResultCode code) {
    Ensure.notNull(object, code, null);
  }
}
