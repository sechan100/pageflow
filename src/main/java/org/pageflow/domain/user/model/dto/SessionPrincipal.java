package org.pageflow.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}의 principal의 타입으로 사용됨.
 * @author : sechan
 */
@Getter
@Builder
@AllArgsConstructor
public class SessionPrincipal {
    private Long UID;
}
