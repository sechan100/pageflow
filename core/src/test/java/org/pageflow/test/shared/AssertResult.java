package org.pageflow.test.shared;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.pageflow.common.result.ResultException;
import org.pageflow.common.result.code.ResultCode;

/**
 * @author : sechan
 */
public abstract class AssertResult {

  public static void assertCode(Executable fn, ResultCode expectedCode) {
    ResultException ex = Assertions.assertThrows(ResultException.class, fn);
    Assertions.assertNotNull(ex.getResult());
    Assertions.assertEquals(expectedCode, ex.getResult().getCode());
  }

}
