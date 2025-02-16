package org.pageflow.common.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public enum RoleType {
  ROLE_ADMIN,
  ROLE_MANAGER,
  ROLE_USER,
  ROLE_ANONYMOUS;

  // RoleType 값 배열을 받아서 spring security 스펙에 맞는 Collection<? extends GrantedAuthority>타입으로 반환한다.
  public static Collection<? extends GrantedAuthority> toAuthorities(RoleType... roles) {
    return Arrays.stream(roles).map(
      role -> new SimpleGrantedAuthority(role.name())
    ).toList();
  }

  public static Collection<? extends GrantedAuthority> toAuthorities(String... roles) {
    return Arrays.stream(roles)
      .map(SimpleGrantedAuthority::new)
      .toList();
  }

  public static RoleType getRoleType(Collection<? extends GrantedAuthority> authorities) {
    return authorities.stream()
      .map(GrantedAuthority::getAuthority)
      .map(RoleType::valueOf)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("해당하는 권한 수준이 없습니다."));
  }

  public Collection<? extends GrantedAuthority> toAuthorities() {
    return toAuthorities(this);
  }
}
