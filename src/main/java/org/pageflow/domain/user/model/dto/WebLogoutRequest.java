package org.pageflow.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : sechan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebLogoutRequest {
    
    private String refreshToken;
    
}
