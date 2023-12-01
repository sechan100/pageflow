package org.pageflow.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
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

    @Column(columnDefinition = "TEXT")
    private String profileImgUrl;

    @OneToOne(
            optional = false,
            fetch = FetchType.LAZY,
            mappedBy = "profile"
    )
    @JsonIgnore
    private Account account;
}
