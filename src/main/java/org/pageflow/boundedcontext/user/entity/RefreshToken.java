package org.pageflow.boundedcontext.user.entity;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.global.entity.TsidIdentifiable;
import org.pageflow.shared.libs.Tsid;


/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken implements org.pageflow.global.entity.Entity, TsidIdentifiable {

    @Id
    @Tsid
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;
    
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
