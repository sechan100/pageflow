package org.pageflow.boundedcontext.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.dto.utils.EncodedPassword;
import org.pageflow.shared.data.entity.BaseEntity;
import org.pageflow.shared.data.entity.TsidIdentifiable;
import org.pageflow.shared.data.tsid.Tsid;
import org.pageflow.shared.type.TSID;
import org.springframework.lang.Nullable;

@Entity
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account", uniqueConstraints = {
    @UniqueConstraint(name = "uk_account_username", columnNames = {"username"}),
})
public class AccountEntity extends BaseEntity implements TsidIdentifiable {

    /**
     * HACK: Profile에서 @MapsId를 이용해서 외래키를 매핑하려면, @Id 칼럼이 부모가 아닌 Account 자체에 위치해야만한다.
     */
    @Id
    @Tsid
    @Getter(AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @Getter(AccessLevel.NONE)
    @Column(nullable = false)
    private String password;

    @Setter(AccessLevel.NONE)
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

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "account")
    private ProfileEntity profile;


    /**
     * @param username 사용자명
     * @param encodedPassword 암호화된 비밀번호
     * @param email 이메일
     * @param provider 소셜 로그인 제공자
     * @param role 사용자 권한
     */
    public AccountEntity(@Nullable TSID id, String username, EncodedPassword encodedPassword, String email, ProviderType provider, RoleType role) {
        this.id = id != null ? id.toLong() : null;
        this.username = username;
        this.password = encodedPassword.getEncodedPassword();
        this.email = email;
        this.emailVerified = false;
        this.provider = provider;
        this.role = role;
    }

    @Override
    public TSID getId() {
        return TSID.from(id);
    }

    // 이메일을 변경하고, 인증 상태를 초기화한다.
    public void changeEmailAndUnVerify(String email) {
        this.email = email;
        this.emailVerified = false;
    }

    public void associateProfile(ProfileEntity profile) {
        this.profile = profile;
        // Profile이 참조하는 account이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(profile.getAccount() == null || !profile.getAccount().equals(this)) {
            profile.associateAccount(this);
        }
    }

    public EncodedPassword getPassword() {
        return new EncodedPassword(password);
    }
}
