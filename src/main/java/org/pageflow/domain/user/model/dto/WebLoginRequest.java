package org.pageflow.domain.user.model.dto;

import jakarta.validation.constraints.NotBlank;
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
public class WebLoginRequest {
    
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}
