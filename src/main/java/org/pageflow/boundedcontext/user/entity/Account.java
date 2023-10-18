package org.pageflow.boundedcontext.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseEntity {
    
    private String provider;
    
    @Column(unique = true)
    private String username;
    
    private String password;
    
    private String role;
    
    /**
     * ORM에서 참조가 꼬이는 것을 막기위해서 일단은 반드시 Account 엔티티를 통해서만 참조하는 것으로 한다.
     */
    @MapsId
    @OneToOne(
            optional = false,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Profile profile;
    
}
