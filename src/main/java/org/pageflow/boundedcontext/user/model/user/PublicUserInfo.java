package org.pageflow.boundedcontext.user.model.user;

import lombok.Builder;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
@Builder
public class PublicUserInfo {
    private Long UID;
    private String username;
    private String email;
    private String penname;
}
