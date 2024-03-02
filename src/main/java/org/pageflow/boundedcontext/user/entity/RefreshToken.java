package org.pageflow.boundedcontext.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.global.entity.BaseEntity;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken implements BaseEntity {
    
    public static final String COOKIE_NAME = "pageflowRefreshTokenUUID";
    
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
    private Long expiredAt;
    
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiredAt;
    }
    
}
