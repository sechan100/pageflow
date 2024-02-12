package org.pageflow.infra.jwt.dto;

import lombok.Builder;

/**
 * @author : sechan
 */
@Builder
public record AccessTokenDto(String accessToken, Long expiredAt){}
