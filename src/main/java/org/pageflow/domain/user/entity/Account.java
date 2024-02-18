package org.pageflow.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.global.entity.NoIdBaseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Entity
@Getter
@Setter(AccessLevel.NONE)
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(indexes = {
        @Index(name = "idx_account_username", columnList = "username", unique = true)
})
public class Account extends NoIdBaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UID;
    
    @Column(unique = true, nullable = false, updatable = false)
    private String username;
    
    private String password;
    
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
    @Column(nullable = false, updatable = false)
    private RoleType role;
    
    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "account")
    private Profile profile;
    
    @Column(nullable = false)
    private String email;
    
    private boolean emailVerified;
    
    
    
    
    // 이메일을 변경하고, 인증 상태를 초기화한다.
    public void changeEmail(String email) {
        this.email = email;
        this.emailVerified = false;
    }
    
    public void verifyEmail() {
        this.emailVerified = true;
    }
    
    public void changePassword(String password) {
        this.password = password;
    }
    
    public boolean isPasswordMatched(String password) {
        return new BCryptPasswordEncoder().matches(password, this.password);
    }
    
    public void associateProfile(Profile profile) {
        this.profile = profile;
        // Profile이 참조하는 account이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(profile.getAccount() == null || !profile.getAccount().equals(this)) {
            profile.associateAccount(this);
        }
    }
    
}
