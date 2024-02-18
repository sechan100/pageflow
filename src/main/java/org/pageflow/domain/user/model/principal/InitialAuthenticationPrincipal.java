package org.pageflow.domain.user.model.principal;


import lombok.Getter;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


/**
 * Spring security의 UserDetails와 OAuth2User의 구현체.<br>
 * 최초 로그인시에, FormLogin과 OAuth2Login의 Principal 반환타입 규격을 모두 충족시키기 위해 사용된다.<br>
 * 이후, AccessToken을 이용하여 유지되는 세션은 {@link SessionPrincipal}을 사용한다.
 */
@Getter
public class InitialAuthenticationPrincipal extends User implements OAuth2User, PageflowPrincipal {

    private final Long UID;
    private final String username;
    private final RoleType role;
    
    public InitialAuthenticationPrincipal(Long UID, String username, String password, RoleType roleType) {
        super(username, password, RoleType.getAuthorities(roleType));
        this.UID = UID;
        this.username = username;
        this.role = roleType;
    }
    
    public static InitialAuthenticationPrincipal from(Account user) {
        return new InitialAuthenticationPrincipal(user.getUID(), user.getUsername(), user.getPassword(), user.getRole());
    }
    
    public static InitialAuthenticationPrincipal anonymous() {
        return new InitialAuthenticationPrincipal(
                0L,
                "anonymous",
                "",
                RoleType.ROLE_ANONYMOUS
        );
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
