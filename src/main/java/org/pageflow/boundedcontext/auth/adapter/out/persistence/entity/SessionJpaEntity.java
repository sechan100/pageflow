package org.pageflow.boundedcontext.auth.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.AccountJpaEntity;
import org.pageflow.shared.jpa.JpaEntity;


/**
 * @author : sechan
 */
@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "session")
public class SessionJpaEntity implements JpaEntity {

    @Id
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AccountJpaEntity account;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Embedded
    private RefreshTokenJpaEmbedded refreshToken;


    public boolean isExpired() {
        return System.currentTimeMillis() > refreshToken.getExp();
    }
    
}
