package org.pageflow.infra.jwt.token;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.domain.user.constants.RoleType;

import java.util.Date;


@Getter
@Setter
@Builder
public class AccessToken {
    
    private Long UID;
    private RoleType role;
    private String token;
    private Date exp;
    private Date iat;
    
}
