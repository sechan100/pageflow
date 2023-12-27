package org.pageflow.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.pageflow.base.entity.DefaultBaseEntity;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenSession extends DefaultBaseEntity {
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;
    
    @Column(nullable = false, updatable = false, unique = true)
    private String sessionKey;
    
    private String refreshToken;
    
}
