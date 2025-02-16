package org.pageflow.user.adapter.in.auth.form;

import org.pageflow.common.api.UriPrefix;

/**
 * {@link UriPrefix}의 PRIVATE uri에 접근하려고 시도한 경우 발생한다.
 * @author : sechan
 */
public class PrivateUriAccessException extends RuntimeException {

  public PrivateUriAccessException(String uri) {
    super(String.format("private url에 대한 외부요청이 발생하였습니다. uri: %s", uri));
  }

}
