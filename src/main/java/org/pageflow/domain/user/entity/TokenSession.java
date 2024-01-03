package org.pageflow.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenSession implements BaseEntity {
    
    /**
     * UUID
     */
    @Id
    private String id;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;
    
    /**
     * 만료시간(UTC)
     */
    @Column(nullable = false)
    private Long expiredIn;
    
    private String refreshToken;
    
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiredIn;
    }
    
}
