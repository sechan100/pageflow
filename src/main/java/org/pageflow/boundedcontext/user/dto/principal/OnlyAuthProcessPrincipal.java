package org.pageflow.boundedcontext.user.dto.principal;


import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.pageflow.boundedcontext.user.dto.utils.EncodedPassword;
import org.pageflow.shared.type.TSID;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;


/**
 * Spring security의 UserDetails와 OAuth2User의 구현체.<br>
 * 최초 로그인시에, FormLogin과 OAuth2Login의 Principal 반환타입 규격을 모두 충족시키기 위해 사용된다.<br>
 * 이후, AccessToken을 이용하여 유지되는 세션은 {@link SessionPrincipal}을 사용한다.
 */
public class OnlyAuthProcessPrincipal extends User implements OAuth2User, PageflowPrincipal {

    private final TSID UID;
    private final String username;
    private final RoleType role;
    
    public OnlyAuthProcessPrincipal(TSID UID, String username, EncodedPassword password, RoleType roleType) {
        super(username, password.getEncodedPassword(), RoleType.getAuthorities(roleType));
        this.UID = UID;
        this.username = username;
        this.role = roleType;
    }
    
    public static OnlyAuthProcessPrincipal from(AccountEntity user) {
        return new OnlyAuthProcessPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRole()
        );
    }

    /**
     * <p>해당 클래스는 오직 Spring Security 인증과정에서만 사용되기 때문에,
     * 더미 객체를 반환한다는 것은 Spring Security의 인증절차에 반드시 실패하게 된다는 것을 의미한다.</p>
     * <p>토큰 기반으로 인증할 때, OAuth2 로그인과정에서 토큰을 반환하는 컨트롤러로 포워딩한대.
     * 이 때, Spring Security OAuth2 인증로직이 비정상적으로 종료되는 것을 막기위해서 더미를 반환할 필요가 있다.</p>
     */
    public static OnlyAuthProcessPrincipal dummy() {
        return new OnlyAuthProcessPrincipal(
                new TSID(0),
                "anonymous",
                new EncodedPassword("dummay"),
                RoleType.ROLE_ANONYMOUS
        );
    }

    @Override
    public TSID getUID(){
        return UID;
    }

    public RoleType getRole(){
        return role;
    }

    @Override
    public Map<String, Object> getAttributes(){
        throw new UnsupportedOperationException("해당 클래스는 타입 호환을 위한 래퍼 클래스입니다. OAuth2 로그인을 지원하지 않습니다.");
    }

    @Override
    public String getName(){
        return username;
    }

    public String getUsername(){
        return username;
    }
}
