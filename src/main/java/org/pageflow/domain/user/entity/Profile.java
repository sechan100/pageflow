package org.pageflow.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Profile {
    
    /**
     * Account의 id와 동일함을 보장하지 않음
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    private String nickname;
    
}
