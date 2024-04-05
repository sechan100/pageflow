package org.pageflow.boundedcontext.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.model.utils.EncodedPassword;
import org.pageflow.global.entity.AuditingBaseEntity;
import org.pageflow.global.entity.TsidIdentifiable;
import org.pageflow.shared.libs.Tsid;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
    @Index(name = "idx_account_username", columnList = "username", unique = true)
})
public class Account extends AuditingBaseEntity implements TsidIdentifiable {

    /**
     * HACK: Profile에서 @MapsId를 이용해서 외래키를 매핑하려면, @Id 칼럼이 부모가 아닌 Account 자체에 위치해야만한다.
     */
    @Id
    @Tsid
    @Setter
    @Getter(AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @Getter(AccessLevel.NONE)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;
    
    /**
     * NATIVE, GOOGLE, KAKAO, NAVER, GITHUB
     * 영속화시 null이라면 NATIVE로 초기화한다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ProviderType provider;
    
    /**
     * ROLE_ADMIN, ROLE_MANAGER, ROLE_USER, ROLE_ANONYMOUS
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;
    
    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "account")
    private Profile profile;


    /**
     * @param username 사용자명
     * @param encodedPassword 암호화된 비밀번호
     * @param email 이메일
     * @param provider 소셜 로그인 제공자
     * @param role 사용자 권한
     */
    public Account(String username, EncodedPassword encodedPassword, String email, ProviderType provider, RoleType role) {
        this.username = username;
        this.password = encodedPassword.getEncodedPassword();
        this.email = email;
        this.emailVerified = false;
        this.provider = provider;
        this.role = role;
    }

    @Override
    public TSID getId(){
        return TSID.from(id);
    }

    // 이메일을 변경하고, 인증 상태를 초기화한다.
    public void changeEmailAndUnVerify(String email) {
        this.email = email;
        this.emailVerified = false;
    }
    
    public void verifyEmail() {
        this.emailVerified = true;
    }
    
    public void changePassword(String password) {
        this.password = password;
    }

    /**
     * @param encoder PasswordEncoder
     * @param rowPassword 인코딩된 비밀번호가 아닌, 사용자가 입력한 비밀번호
     */
    public boolean isPasswordMatched(PasswordEncoder encoder, String rowPassword) {
        return encoder.matches(rowPassword, this.password);
    }
    
    public void associateProfile(Profile profile) {
        this.profile = profile;
        // Profile이 참조하는 account이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(profile.getAccount() == null || !profile.getAccount().equals(this)) {
            profile.associateAccount(this);
        }
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public EncodedPassword getEncodedPassword() {
        return new EncodedPassword(password);
    }
}
