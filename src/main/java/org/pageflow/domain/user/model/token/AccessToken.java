package org.pageflow.domain.user.model.token;

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
    private String compact;
    private Date exp;
    private Date iat;
    
}
