package org.pageflow.common.api;

/**
 * 다양한 보안 정책을 적용한 uri들의 prefix를 정의하는 스펙
 *
 * 보안 정책의 구현은 주로 auth 모듈의 spring security filter에서 구현된다.
 * @author : sechan
 */
public abstract class UriPrefix {
  /**
   * PRIVATE prefix는 오직 forward를 통해서만 접근 가능하다.
   */
  public static final String PRIVATE = "/PRIVATE";

  public static String PRIVATE(String uri) {
    return PRIVATE + uri;
  }
}
