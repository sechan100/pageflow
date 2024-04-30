package org.pageflow.boundedcontext.user.adapter.out.persistence.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.shared.jpa.BaseJpaEntity;

@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "account", indexes = {
    @Index(name = "idx_account_username", columnList = "username")
})
public class AccountJpaEntity extends BaseJpaEntity {

    /**
     * HACK: Profile에서 @MapsId를 이용해서 외래키를 매핑하려면, @Id 칼럼이 부모가 아닌 Account 자체에 위치해야만한다.
     */
    @Id
    @Setter(AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;

    /**
     * NATIVE, GOOGLE, KAKAO, NAVER, GITHUB
     * 영속화시 null이라면 NATIVE로 초기화한다.
     */
    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
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
    @OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileJpaEntity profile;


    public void associateProfile(ProfileJpaEntity profile) {
        this.profile = profile;
        // Profile이 참조하는 account이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(profile.getAccount() == null || !profile.getAccount().equals(this)) {
            profile.associateAccount(this);
        }
    }
}
