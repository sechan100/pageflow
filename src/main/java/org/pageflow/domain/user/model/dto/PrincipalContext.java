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
// TODO: 이름 바꾸기 -> ComplextPrincipal
public class PrincipalContext extends User implements OAuth2User {

    protected Long id;
    protected String username;
    protected RoleType role;
    
    public PrincipalContext(Long UID, String username, String password, RoleType roleType) {
        super(username, password, RoleType.getAuthorities(roleType));
        this.id = UID;
        this.username = username;
        this.role = roleType;
    }
    
    public static PrincipalContext from(Account user) {
        return new PrincipalContext(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }
    
    // Role 임의 지정
    public static PrincipalContext from(Account user, RoleType roleType) {
        return new PrincipalContext(user.getId(), user.getUsername(), user.getPassword(), roleType);
    }
    
    public static PrincipalContext anonymous() {
        return new PrincipalContext(
                0L,
                "anonymous",
                "",
                RoleType.ROLE_ANONYMOUS);
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return getUsername();
    }

}
