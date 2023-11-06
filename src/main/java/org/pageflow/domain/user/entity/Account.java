package org.pageflow.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    
    private String provider;
    
    @Column(unique = true)
    private String username;
    
    private String password;
    
    private String email;
    
    private String role;
    
    @OneToOne(
            optional = false,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private Profile profile;

}
