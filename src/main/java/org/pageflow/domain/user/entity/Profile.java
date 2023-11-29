package org.pageflow.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String profileImgUrl;

    @OneToOne(
            optional = false,
            fetch = FetchType.LAZY,
            mappedBy = "profile"
    )
    @JsonIgnore
    private Account account;

}
