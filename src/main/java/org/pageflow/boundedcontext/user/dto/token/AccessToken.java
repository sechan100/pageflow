package org.pageflow.boundedcontext.user.dto.token;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.shared.type.TSID;

import java.util.Date;


@SuppressWarnings("UseOfObsoleteDateTimeApi") @Getter
@Setter
@Builder
public class AccessToken {
    
    private TSID UID;
    private RoleType role;
    private String compact;
    private Date exp;
    private Date iat;
    
}
