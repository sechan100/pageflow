package org.pageflow.boundedcontext.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.global.data.AutoAuditingEntity;


@Entity
@Getter
@Setter(AccessLevel.NONE)
@Builder
@EqualsAndHashCode(of = "uid", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends AutoAuditingEntity {
    
    // @MapsId: Account의 PK를 Profile의 PK로 사용
    @Id
    private Long uid;
    
    // 필명
    @Column(unique = true, nullable = false)
    private String penname;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImgUrl;
    
    @JsonIgnore
    @MapsId
    @JoinColumn(name = "uid")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    
    public void changePenname(String penname) {
        this.penname = penname;
    }
    
    public void changeProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
    
    public void associateAccount(Account account) {
        this.account = account;
        // Account가 참조하는 profile이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(account.getProfile() == null || !account.getProfile().equals(this)) {
            account.associateProfile(this);
        }
    }
    
}