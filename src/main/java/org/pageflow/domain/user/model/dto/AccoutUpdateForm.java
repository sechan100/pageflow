package org.pageflow.domain.user.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author : sechan
 */
@Data
@Builder
public class AccoutUpdateForm implements AccountDto {
    
    private String provider;
    private String username;
    private String email;
    private String password;

    
}
