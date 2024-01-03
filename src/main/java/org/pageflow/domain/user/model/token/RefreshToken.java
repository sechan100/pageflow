package org.pageflow.domain.user.model.token;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author : sechan
 */
@Getter
@Setter
@SuperBuilder
public class RefreshToken extends AbstractSessionToken {
    private Long UID;
    private String sessionId;
}
