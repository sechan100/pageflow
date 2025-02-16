package org.pageflow.user.adapter.in.auth.oauth2.authorizationrequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * OAuth2 인증요청(OAuth2AuthorizationRequest)을 저장하고 관리하는 메커니즘을 정의한다.
 * OAuth2 로그인 플로우 중에 생성되는 인증 요청 객체를 저장하고, 검색한다.
 * 클라이언트가 인증 서버에 대해 발급한 redirect_uri, scope, state, 임시 코드(challenge) 등이 포함된다.
 *
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DefaultAuthorizationRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
  private final AuthorizationRequestEntityJpaRepository repository;


  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return load(request).getData();
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
    repository.save(OAuth2AUthorizationRequestEntity.of(
      authorizationRequest.getState(),
      authorizationRequest
    ));
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
    OAuth2AUthorizationRequestEntity entity = load(request);
    repository.delete(entity);
    return entity.getData();
  }


  private OAuth2AUthorizationRequestEntity load(HttpServletRequest request) {
    String state = getStateParameter(request);
    OAuth2AUthorizationRequestEntity entity = repository.findById(state)
      .orElseThrow(() -> new IllegalStateException("OAuth2AuthorizationRequest의 state로 저장된 request를 찾을 수 없습니다." + state));
    return entity;
  }

  private String getStateParameter(HttpServletRequest request) {
    String state = request.getParameter("state");
    Assert.hasLength(state, "oatuh2 authorizationRequest state cannot be null");
    return state;
  }
}
