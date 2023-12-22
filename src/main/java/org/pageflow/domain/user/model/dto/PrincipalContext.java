package org.pageflow.domain.user.model.dto;


import lombok.Getter;
import lombok.Setter;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


/**
 * Spring security Principal 규격의 사용자 구현.
 * User클래스와 OAuth2User 인터페이스의 상속과 구현을 통해, FormLogin과 OAuth2Login의 Principal 규격을 모두 충족시킨다.
 * 사실상 Holder의 역할이며, 필드의 UserDto 객체를 통해 실제 사용자 데이터에 접근한다.
 */
@Getter
@Setter
public class PrincipalContext extends User implements OAuth2User {

    protected UserDto userDto;
    
    public PrincipalContext(Account user) {
        super(user.getUsername(), null, RoleType.getAuthorities(user.getRole()));
        this.userDto = UserDto.from(user);
    }
    
    // Role을 강제로 변경하여 저장
    public PrincipalContext(Account user, RoleType roleType) {
        super(user.getUsername(), "", RoleType.getAuthorities(roleType));
        this.userDto = UserDto.from(user);
        this.userDto.setRole(roleType);
    }
    
    public PrincipalContext(UserDto userDto) {
        super(userDto.getUsername(), "", RoleType.getAuthorities(userDto.getRole()));
        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return userDto.getUsername();
    }

}
