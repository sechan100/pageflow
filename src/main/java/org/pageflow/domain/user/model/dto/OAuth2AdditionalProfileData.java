package org.pageflow.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OAuth2AdditionalProfileData {
    
    private String nickname;
    private String profileImgUrl;
    
}