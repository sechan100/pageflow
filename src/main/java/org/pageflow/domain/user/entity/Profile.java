package org.pageflow.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {
    
    private String nickname;
    
    @Transient
    private String profileImgUrl;
    
}
