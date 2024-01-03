package org.pageflow.domain.user.model.token;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.pageflow.domain.user.constants.RoleType;


@Getter
@Setter
@SuperBuilder
public class AccessToken extends AbstractSessionToken {
    
    private Long UID;
    private String username;
    private RoleType role;
    
}
