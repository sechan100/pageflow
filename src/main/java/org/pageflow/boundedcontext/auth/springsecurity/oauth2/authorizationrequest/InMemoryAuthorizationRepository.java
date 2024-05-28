package org.pageflow.boundedcontext.auth.springsecurity.oauth2.authorizationrequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.pageflow.global.api.ResDataTypes;
import org.pageflow.global.api.code.ApiCode4;
import org.pageflow.global.api.exception.ApiException;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * OAuth2 인증요청(OAuth2AuthorizationRequest)을 저장하고 관리하는 메커니즘을 정의한다.
 * OAuth2 로그인 플로우 중에 생성되는 인증 요청 객체를 저장하고, 검색한다.
 * 클라이언트가 인증 서버에 대해 발급한 redirect_uri, scope, state, 임시 코드(challenge) 등이 포함된다.
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class InMemoryAuthorizationRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    
    private final AuthorizationRequestRedisRepo repository;
    
    
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return loadCache(request).getRequest();
    }
    
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        repository.save(
                RedisOAuth2AuthorizationRequestWrapper.builder()
                        .state(authorizationRequest.getState())
                        .request(authorizationRequest)
                        .build()
        );
    }
    
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        RedisOAuth2AuthorizationRequestWrapper cache = loadCache(request);
        repository.delete(cache);
        return cache.getRequest();
    }


    private RedisOAuth2AuthorizationRequestWrapper loadCache(HttpServletRequest request) {
        String state = getStateParameter(request);
        var wrapper = repository.findById(state)
                .orElseThrow(() -> new ApiException(
                    ApiCode4.BAD_CREDENTIALS,
                    new ResDataTypes.FieldName("oauth2-state")
                ));
        repository.delete(wrapper);
        return wrapper;
    }

    private String getStateParameter(HttpServletRequest request) {
        String state = request.getParameter("state");
        Assert.hasLength(state, "state cannot be null");
        return state;
    }
}
