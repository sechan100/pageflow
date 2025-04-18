package org.pageflow.user.adapter.in.auth.oauth2.owner;




import org.pageflow.common.user.ProviderType;

import java.util.Map;

/**
 * 소셜 로그인 리소스서버별로 제공하는 표준화되지 않은 유저 정보를 표준화하기 위한 인터페이스
 */
public interface OAuth2ResourceOwner {

  String getId();

  String getUsername();

  String getEmail();

  String getProvider();

  String getProfileImgUrl();

  String getNickname();

  ProviderType getProviderType();

  Map<String, Object> getAttributes();

}
