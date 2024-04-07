package org.pageflow.boundedcontext.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.shared.data.entity.BaseEntity;
import org.pageflow.shared.data.entity.TsidIdentifiable;
import org.pageflow.shared.type.TSID;


@Entity
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profile")
public class ProfileEntity extends BaseEntity implements TsidIdentifiable {
    
    // @MapsId: Account의 PK를 Profile의 PK로 사용
    @Id
    @Getter(AccessLevel.NONE)
    private Long id;
    
    // 필명
    @Column(unique = true, nullable = false)
    private String penname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profileImgUrl;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @MapsId
    @JoinColumn(name = "id")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private AccountEntity account;

    public ProfileEntity(String penname, String profileImgUrl) {
        this.penname = penname;
        this.profileImgUrl = profileImgUrl;
    }

    @Override
    public TSID getId() {
        return TSID.from(id);
    }
    
    public void associateAccount(AccountEntity account) {
        this.account = account;
        // Account가 참조하는 profile이 null이거나, 자기 자신이 아니라면 참조를 갱신
        if(account.getProfile() == null || !account.getProfile().equals(this)) {
            account.associateProfile(this);
        }
    }
}
