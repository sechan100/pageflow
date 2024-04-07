package org.pageflow.boundedcontext.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.shared.data.entity.TsidIdentifiable;
import org.pageflow.shared.data.tsid.Tsid;
import org.pageflow.shared.type.TSID;


/**
 * @author : sechan
 */
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken implements org.pageflow.shared.data.entity.Entity, TsidIdentifiable {

    @Id
    @Tsid
    @Getter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AccountEntity account;
    
    /**
     * 만료시간(UTC)
     */
    @Column(nullable = false)
    private Long expiredAt;

    @Override
    public TSID getId() {
        return TSID.from(id);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiredAt;
    }
    
}
