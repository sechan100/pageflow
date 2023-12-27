package org.pageflow.domain.user.constants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;

public enum RoleType {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_ANONYMOUS,
    ROLE_MANAGER;

    
    // RoleType 값 배열을 받아서 spring security 스펙에 맞는 Collection<? extends GrantedAuthority>타입으로 반환한다.
    public static Collection<? extends GrantedAuthority> getAuthorities(RoleType... roles) {
        Assert.notNull(roles, "권한 수준 문자열 배열 roles는 null일 수 없습니다.");
        
        return Arrays.stream(roles).map(
                role -> new SimpleGrantedAuthority(role.name())
        ).toList();
    }
    
    public static Collection<? extends GrantedAuthority> getAuthorities(String... roles) {
        Assert.notNull(roles, "권한 수준 문자열 배열 roles는 null일 수 없습니다.");
        
        return Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
    
    public static RoleType getRoleType(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "권한 수준 문자열 배열 authorities는 null일 수 없습니다.");
        
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(RoleType::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 권한 수준이 없습니다."));
    }
}
