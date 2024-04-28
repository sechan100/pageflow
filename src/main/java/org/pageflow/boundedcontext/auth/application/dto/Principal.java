package org.pageflow.boundedcontext.auth.application.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.EncryptedPassword;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.common.value.UID;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * @author : sechan
 */
public abstract class Principal {
    public interface Base {
        UID getUid();
        RoleType getRole();
    }

    /**
     * Spring security의 UserDetails와 OAuth2User의 구현체.<br>
     * 최초 로그인시에, FormLogin과 OAuth2Login의 Principal 반환타입 규격을 모두 충족시키기 위해 사용된다.<br>
     * 이후, AccessToken을 이용하여 유지되는 세션은 {@link Session}을 사용한다.
     */
    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class OnlyInAuthing extends User implements OAuth2User, Base {
        private Account authedAccount;
        /**
         * 이미 다른 로직으로 분기했지만, SpringSecurity 스펙상 더미로 반환한 principla 객체때문에
         * AuthenticationSuccessHandler가 호출되는 경우를 위한 flag
         */
        private boolean isInFilterForwarded;

        private OnlyInAuthing(Account authedAccount, boolean isInFilterForwarded) {
            super(
                authedAccount.getUsername(),
                authedAccount.getPassword().getValue(),
                RoleType.toAuthorities(authedAccount.getRole())
            );
            this.authedAccount = authedAccount;
            this.isInFilterForwarded = isInFilterForwarded;
        }

        public OnlyInAuthing(Account authedAccount) {
            this(authedAccount, false);
        }

        /**
         * <p>해당 클래스는 오직 Spring Security 인증과정에서만 사용되기 때문에,
         * 더미 객체를 반환한다는 것은 Spring Security의 인증절차에 반드시 실패하게 된다는 것을 의미한다.</p>
         * <p>토큰 기반으로 인증할 때, OAuth2 로그인과정에서 토큰을 반환하는 컨트롤러로 포워딩한대.
         * 이 때, Spring Security OAuth2 인증로직이 비정상적으로 종료되는 것을 막기위해서 더미를 반환할 필요가 있다.</p>
         */
        public static OnlyInAuthing dummy() {
            return new OnlyInAuthing(
                new Account(
                    UID.from(0L),
                    "dummy",
                    EncryptedPassword.of("dummy"),
                    "dummy",
                    RoleType.ROLE_ANONYMOUS
                ),
                true
            );
        }

        @Override
        public Map<String, Object> getAttributes(){
            throw new UnsupportedOperationException("해당 클래스는 타입 호환을 위한 Wrapper입니다. " +
                "해당 메소드는 타입 호환을 위해 구현한 OAuth2User의 스펙이며, 해당 구현에서는 이를 지원하지 않습니다.");
        }

        @Override
        public String getName(){
            return getUsername();
        }

        @Override
        public UID getUid() {
            return authedAccount.getUid();
        }

        @Override
        public RoleType getRole() {
            return authedAccount.getRole();
        }
    }

    /**
     * 이미 존재하는 세션에 AccessToken으로 인증한 사용자의 principal 타입으로 사용됨.
     * @author : sechan
     */
    @Value
    public static class Session implements Base {
        UID uid;
        RoleType role;

        public static Session anonymous() {
            return new Session(
                UID.from(0L),
                RoleType.ROLE_ANONYMOUS
            );
        }
    }

}
