package org.pageflow.domain.user.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author         : sechan
 *
 */
@Data
public class ProfileUpdateForm {
    
    private Long id;
    
    @NotEmpty
    @Size(min = 2, max = 10)
    private String nickname;
    
    private MultipartFile profileImg;
}
