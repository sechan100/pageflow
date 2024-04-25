package org.pageflow.boundedcontext.user.adapter.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.shared.jpa.BaseJpaEntity;


@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "profile")
public class ProfileJpaEntity extends BaseJpaEntity {
    
    // @MapsId: Account의 PK를 Profile의 PK로 사용
    @Id
    @Setter(AccessLevel.NONE)
    private Long id;
    
    // 필명
    @Column(nullable = false)
    private String penname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImageUrl;

    @JsonIgnore
    @MapsId
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "id")
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AccountJpaEntity account;
    
    public void associateAccount(AccountJpaEntity account) {
        this.account = account;
        // Account가 참조하는 profile이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(account.getProfile() == null || !account.getProfile().equals(this)) {
            account.associateProfile(this);
        }
    }
}
