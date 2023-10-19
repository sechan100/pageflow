package org.pageflow.boundedcontext.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    
    private String provider;
    
    @Column(unique = true)
    private String username;
    
    private String password;
    
    private String email;
    
    private String role;
    
    /**
     * ORM 상에서의 일관된 참조 관계를 위해서, 막기위해서 일단은 반드시 Account 엔티티를 통해서만 참조하는 것으로 한다.
     */
    @MapsId
    @OneToOne(
            optional = false,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Profile profile;
    
}
