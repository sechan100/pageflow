package org.pageflow.boundedcontext.user.model.token;

import io.hypersistence.tsid.TSID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.boundedcontext.user.constants.RoleType;

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
