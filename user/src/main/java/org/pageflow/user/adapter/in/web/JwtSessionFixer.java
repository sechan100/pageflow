package org.pageflow.user.adapter.in.web;

/**
 * 개발환경에서 api 요청 테스트의 편리를 위해서 jwt sesstion을 고정시키는 인터페이스
 * @author : sechan
 */
public interface JwtSessionFixer {
  void fixSession(String username);
  void clearFixedPrincipal();
}
